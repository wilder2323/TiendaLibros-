package co.edu.uptc.datos;

import co.edu.uptc.modelo.Cliente;
import co.edu.uptc.modelo.Compra;
import co.edu.uptc.modelo.ItemCarrito;
import co.edu.uptc.modelo.Libro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Lee y escribe los archivos JSON de la tienda (libros, clientes y compras)
// usando la librería Gson dependencia de Maven
// Los archivos viven o se almacenan en la carpeta "data" (en la raíz del proyecto, junto al pom.xml)
// si no existen, se crean solos la primera vez que se guarda algo.
public class AlmacenJson {

    private static final Path CARPETA = Paths.get("data");
    private static final Path ARCHIVO_LIBROS = CARPETA.resolve("libros.json");
    private static final Path ARCHIVO_CLIENTES = CARPETA.resolve("clientes.json");
    private static final Path ARCHIVO_COMPRAS = CARPETA.resolve("compras.json");

    // Gson con sangría (para que el archivo sea legible) y sin escapar tildes ni símbolos
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    // Cargar (se llama una sola vez, al abrir la aplicación)
    // las compras necesitan que ya estén cargados
    // los clientes y los libros a los que hacen referencia.
    public static void cargarTodo(Inventario inventario, RepositorioClientes clientes, RepositorioCompras compras) {
        cargarLibros(inventario);
        cargarClientes(clientes);
        cargarCompras(compras, clientes, inventario);
    }

    private static void cargarLibros(Inventario inventario) {
        for (JsonElement e : leerArreglo(ARCHIVO_LIBROS)) {
            JsonObject o = e.getAsJsonObject();
            inventario.listaLibros().add(new Libro(
                    texto(o, "isbn"), texto(o, "titulo"), texto(o, "autor"),
                    entero(o, "anioPublicacion"), texto(o, "categoria"), texto(o, "editorial"),
                    entero(o, "numeroPaginas"), decimal(o, "precio"),
                    entero(o, "cantidadDisponible"), texto(o, "formatoLibro")));
        }
    }

    private static void cargarClientes(RepositorioClientes repositorio) {
        for (JsonElement e : leerArreglo(ARCHIVO_CLIENTES)) {
            JsonObject o = e.getAsJsonObject();
            repositorio.getClientes().add(new Cliente(
                    texto(o, "nombreCompleto"), texto(o, "correo"), texto(o, "contrasena"),
                    texto(o, "direccionEnvio"), texto(o, "telefono"), texto(o, "tipoCliente")));
        }
    }

    private static void cargarCompras(RepositorioCompras repositorio, RepositorioClientes clientes, Inventario inventario) {
        for (JsonElement e : leerArreglo(ARCHIVO_COMPRAS)) {
            JsonObject o = e.getAsJsonObject();
            Cliente cliente = clientes.buscarPorCorreo(texto(o, "correoCliente"));
            if (cliente == null) continue; // compra de un cliente que ya no existe se ignora

            List<ItemCarrito> items = new ArrayList<>();
            for (JsonElement ei : o.getAsJsonArray("libros")) {
                JsonObject oi = ei.getAsJsonObject();
                Libro libro = inventario.buscarPorIsbn(texto(oi, "isbn"));
                if (libro != null) items.add(new ItemCarrito(libro, entero(oi, "cantidad")));
            }

            repositorio.getCompras().add(new Compra(
                    entero(o, "idCompra"), cliente, LocalDate.parse(texto(o, "fecha")), items,
                    decimal(o, "subtotal"), decimal(o, "totalImpuestos"),
                    decimal(o, "descuento"), decimal(o, "totalCompra"), texto(o, "metodoPago")));
        }
    }

    // "guardar", se llama cada vez que cambian los datos

    public static void guardarLibros(Inventario inventario) {
        JsonArray lista = new JsonArray();
        for (Libro l : inventario.listaLibros()) {
            JsonObject o = new JsonObject();
            o.addProperty("isbn", l.getIsbn());
            o.addProperty("titulo", l.getTitulo());
            o.addProperty("autor", l.getAutor());
            o.addProperty("anioPublicacion", l.getAnioPublicacion());
            o.addProperty("categoria", l.getCategoria());
            o.addProperty("editorial", l.getEditorial());
            o.addProperty("numeroPaginas", l.getNumeroPaginas());
            o.addProperty("precio", l.getPrecio());
            o.addProperty("cantidadDisponible", l.getCantidadDisponible());
            o.addProperty("formatoLibro", l.getFormatoLibro());
            lista.add(o);
        }
        escribir(ARCHIVO_LIBROS, lista);
    }

    public static void guardarClientes(RepositorioClientes repositorio) {
        JsonArray lista = new JsonArray();
        for (Cliente c : repositorio.getClientes()) {
            JsonObject o = new JsonObject();
            o.addProperty("nombreCompleto", c.getNombreCompleto());
            o.addProperty("correo", c.getCorreo());
            o.addProperty("contrasena", c.getContraseña());
            o.addProperty("direccionEnvio", c.getDireccionEnvio());
            o.addProperty("telefono", c.getTelefono());
            o.addProperty("tipoCliente", c.getTipoCliente());
            lista.add(o);
        }
        escribir(ARCHIVO_CLIENTES, lista);
    }

    public static void guardarCompras(RepositorioCompras repositorio) {
        JsonArray lista = new JsonArray();
        for (Compra c : repositorio.getCompras()) {
            JsonObject o = new JsonObject();
            o.addProperty("idCompra", c.getIdCompra());
            o.addProperty("correoCliente", c.getCliente().getCorreo());
            o.addProperty("fecha", c.getFechaCompra().toString());
            JsonArray libros = new JsonArray();
            for (ItemCarrito item : c.getLibrosComprados()) {
                JsonObject oi = new JsonObject();
                oi.addProperty("isbn", item.getLibro().getIsbn());
                oi.addProperty("cantidad", item.getCantidad());
                libros.add(oi);
            }
            o.add("libros", libros);
            o.addProperty("subtotal", c.getSubtotal());
            o.addProperty("totalImpuestos", c.getTotalImpuestos());
            o.addProperty("descuento", c.getDescuento());
            o.addProperty("totalCompra", c.getTotalCompra());
            o.addProperty("metodoPago", c.getMetodoPago());
            lista.add(o);
        }
        escribir(ARCHIVO_COMPRAS, lista);
    }

    // Lectura y escritura de archivos

    // Si el archivo no existe todavía (o está dañado), devuelve una lista vacía
    private static JsonArray leerArreglo(Path archivo) {
        if (!Files.exists(archivo)) return new JsonArray();
        try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            JsonElement raiz = JsonParser.parseReader(lector);
            return raiz.isJsonArray() ? raiz.getAsJsonArray() : new JsonArray();
        } catch (IOException | RuntimeException e) {
            System.err.println("No se pudo leer " + archivo + ": " + e.getMessage());
            return new JsonArray();
        }
    }

    private static void escribir(Path archivo, JsonArray datos) {
        try {
            Files.createDirectories(CARPETA);
            String texto = GSON.toJson(datos) + "\n";
            Files.write(archivo, texto.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("No se pudo guardar " + archivo + ": " + e.getMessage());
        }
    }

    // Utilidades para sacar datos de un objeto JSON ya leído
    // (si falta un campo devuelven "" o 0 en vez de romper el programa)

    private static String texto(JsonObject o, String clave) {
        return (o.has(clave) && !o.get(clave).isJsonNull()) ? o.get(clave).getAsString() : "";
    }

    private static int entero(JsonObject o, String clave) {
        return (o.has(clave) && !o.get(clave).isJsonNull()) ? o.get(clave).getAsInt() : 0;
    }

    private static double decimal(JsonObject o, String clave) {
        return (o.has(clave) && !o.get(clave).isJsonNull()) ? o.get(clave).getAsDouble() : 0;
    }
}