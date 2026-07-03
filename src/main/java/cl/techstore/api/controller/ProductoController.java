package cl.techstore.api.controller;

import cl.techstore.api.dto.ProductoDTO;
import cl.techstore.api.model.Producto;
import cl.techstore.api.service.ProductoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody ProductoDTO dto) {
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(dto.getNombre());
        nuevoProducto.setDescripcion(dto.getDescripcion());
        nuevoProducto.setPrecio(dto.getPrecio());
        nuevoProducto.setStock(dto.getStock());
        nuevoProducto.setCategoria(dto.getCategoria());
        nuevoProducto.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        
        Producto productoCreado = productoService.crear(nuevoProducto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> modificar(@PathVariable Long id, @RequestBody ProductoDTO dto) {
        Producto productoActualizado = new Producto();
        productoActualizado.setNombre(dto.getNombre());
        productoActualizado.setDescripcion(dto.getDescripcion());
        productoActualizado.setPrecio(dto.getPrecio());
        productoActualizado.setStock(dto.getStock());
        productoActualizado.setCategoria(dto.getCategoria());
        productoActualizado.setActivo(dto.getActivo());

        Producto productoModificado = productoService.modificar(id, productoActualizado);

        return ResponseEntity.ok(productoModificado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        
        return ResponseEntity.noContent().build();
    }
}