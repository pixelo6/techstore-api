package cl.techstore.api.service;

import cl.techstore.api.model.Producto;
import cl.techstore.api.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import java.time.Instant;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final SqsClient sqsClient;
    private final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/870053463654/techstore-audit-queue";

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
        
        this.sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsSessionCredentials.create(
                                System.getenv("AWS_ACCESS_KEY_ID"), 
                                System.getenv("AWS_SECRET_ACCESS_KEY"),
                                System.getenv("AWS_SESSION_TOKEN") 
                        )
                ))
                .build();
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto crear(Producto producto) {
        Producto p = productoRepository.save(producto);
        System.out.println("--- INTENTANDO ENVIAR A SQS ---");
        enviarAuditoria("CREAR", p);
        return p;
    }

    public Producto modificar(Long id, Producto productoActualizado) {
        Producto p = productoRepository.findById(id).map(producto -> {
            producto.setNombre(productoActualizado.getNombre());
            producto.setDescripcion(productoActualizado.getDescripcion());
            producto.setPrecio(productoActualizado.getPrecio());
            producto.setStock(productoActualizado.getStock());
            producto.setCategoria(productoActualizado.getCategoria());
            producto.setActivo(productoActualizado.getActivo());
            return productoRepository.save(producto);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        enviarAuditoria("MODIFICAR", p);
        return p;
    }

    public void eliminar(Long id) {
        productoRepository.findById(id).ifPresent(producto -> {
            producto.setActivo(false);
            productoRepository.save(producto);
            enviarAuditoria("ELIMINAR", producto);
        });
    }

    private void enviarAuditoria(String accion, Producto p) {
        String jsonMensaje = String.format(
            "{\"accion\": \"%s\", \"productoId\": %d, \"nombre\": \"%s\", \"usuario\": \"admin@techstore.cl\", \"fecha\": \"%s\"}",
            accion, p.getId(), p.getNombre(), Instant.now().toString()
        );

        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .messageBody(jsonMensaje)
                .build());
    }
}