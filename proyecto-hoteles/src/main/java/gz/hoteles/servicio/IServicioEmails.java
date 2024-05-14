package gz.hoteles.servicio;

public interface IServicioEmails {

    void enviarCorreo(String destinatario, String asunto, String contenido);
    
}
