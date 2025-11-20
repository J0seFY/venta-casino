# Sistema de Ventas — Casino Universitario (Java, MVC)

Proyecto académico para practicar Modelado de Procesos e Información con Programación Orientada a Objetos (POO) y el patrón MVC. La aplicación es de consola y simula ventas en el casino de una universidad.

## Objetivos de aprendizaje
- Entender y aplicar MVC con responsabilidades claras.
- Modelar dominio (clientes, productos, ventas, detalles) en POO.
- Persistir datos sencillos usando archivos de texto (TXT).
- Diseñar clases, relaciones y dependencias entre capas.

## Arquitectura (MVC + Persistencia)
- `Controlador`:
  - Orquesta el flujo, recibe entradas desde la vista, usa el modelo y persiste cambios a través de interfaces de repositorio.
  - Clase principal: `Controlador.VentaController` (contiene `main`).
- `Vista` (consola, pasiva):
  - Solo muestra y solicita datos primitivos (String, int, double).
  - No crea objetos de dominio (el controlador construye entidades).
  - Clase: `Vista.VistaConsola`.
- `Modelo` (dominio):
  - Entidades: `Cliente`, `Estudiante`, `Producto`, `Venta`, `DetalleVenta`.
  - Lógica: stock, totales, pago con beca, etc.
  - Servicio de dominio simple: `Tienda` mantiene colecciones en memoria; NO conoce la persistencia.
  - Interfaces de repositorio: `ClientesRepositorio`, `ProductosRepositorio`, `VentasRepositorio`.
- `Persistencia`:
  - Implementación TXT: `Persistencia.RepositorioTxt` que realiza las interfaces del modelo.

## Diagrama de clases
![Diagrama de clases](diagrama_de_clases.svg)

## Estructura
```
Controlador/
  VentaController.java
Modelo/
  Cliente.java
  Estudiante.java
  Producto.java
  DetalleVenta.java
  Venta.java
  Tienda.java
  ClientesRepositorio.java
  ProductosRepositorio.java
  VentasRepositorio.java
Persistencia/
  RepositorioTxt.java
Vista/
  VistaConsola.java
docs/
  diagrama-clases.md
```

## Requisitos
- Java 11 o superior (JDK). Verifica con `java -version` en tu terminal.

## Compilar y ejecutar (Windows PowerShell)
Desde la carpeta raíz del proyecto:
```powershell
javac Controlador/VentaController.java Modelo/*.java Vista/*.java Persistencia/*.java
java Controlador.VentaController
```

## Funcionalidades principales
- Gestión de clientes
  - Listar, crear cliente, crear estudiante (con saldo de beca), buscar por RUT.
- Gestión de productos
  - Listar, agregar, consultar por ID.
- Ventas
  - Iniciar venta para un cliente (o crear uno si no existe).
  - Agregar productos con validación de stock.
  - Finalizar venta y pagar (estudiante: puede usar beca; si no alcanza, efectivo).
- Historial de ventas
  - Consulta de ventas anteriores (se cargan al iniciar la app).

## Persistencia (archivos TXT en `data/`)
- Se generan automáticamente al usar la aplicación.
- Formatos:
  - `data/clientes.txt`
    - Cliente normal: `C;RUT;Nombre`
    - Estudiante: `E;RUT;Nombre;SaldoBeca`
  - `data/productos.txt`
    - `id;nombre;precio;stock`
  - `data/ventas.txt`
    - Cabecera venta: `V;idVenta;rutCliente`
    - Detalle: `D;productoId;cantidad;precioUnitario`
    - Línea en blanco separa ventas

Ejemplo `ventas.txt`:
```
V;1;20373938-9
D;1;2;500.0

V;2;1-9
D;2;3;300.0
D;3;1;1500.0
```

## Flujo de uso rápido
1. Ejecuta la app y elige `9` para cargar datos de ejemplo (opcional).
2. Crea clientes/estudiantes y productos desde los menús 1 y 2.
3. Inicia una venta (opción 3), agrega productos y finaliza.
4. Revisa el historial (opción 4). Reinicia la app para verificar que la persistencia funciona.

## Problemas comunes
- Entrada por consola: si ves `Valor inválido`, vuelve a ingresar el número.
- Rutas: compila/ejecuta desde la carpeta raíz del proyecto.

