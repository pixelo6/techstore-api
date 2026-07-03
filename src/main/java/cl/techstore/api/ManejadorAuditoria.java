package cl.techstore.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

public class ManejadorAuditoria implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent evento, Context contexto) {
        for (SQSEvent.SQSMessage mensaje : evento.getRecords()) {
            contexto.getLogger().log("Procesando evento de auditoría: " + mensaje.getBody());
        }
        return null;
    }
}