package gz.hoteles.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

@Service
public class ServicioEmails implements IServicioEmails {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void enviarCorreo(String destinatario, String asunto, String contenido) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("jcarrillo@aunnait.es");
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(contenido);
        emailSender.send(mensaje);
    }
    
}
