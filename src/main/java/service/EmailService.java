package service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {
    public void enviar(String destinatario, String assunto, String corpo)
            throws MessagingException {
        Properties config = new Properties();
        config.setProperty("mail.smtp.host", variavel("SMTP_HOST").trim());
        config.setProperty("mail.smtp.port", variavel("SMTP_PORT").trim());
        config.setProperty("mail.smtp.auth", "true");
        config.setProperty("mail.smtp.starttls.enable", "true");
        config.setProperty("mail.smtp.starttls.required", "true");
        config.setProperty("mail.smtp.ssl.checkserveridentity", "true");
        config.setProperty("mail.smtp.connectiontimeout", "10000");
        config.setProperty("mail.smtp.timeout", "10000");
        config.setProperty("mail.smtp.writetimeout", "10000");

        Session sessao = Session.getInstance(config);
        MimeMessage mensagem = new MimeMessage(sessao);
        InternetAddress remetente = endereco(variavel("SMTP_FROM").trim());
        InternetAddress destino = endereco(destinatario);

        String utf8 = StandardCharsets.UTF_8.name();
        mensagem.setFrom(remetente);
        mensagem.setRecipient(Message.RecipientType.TO, destino);
        mensagem.setSubject(assunto, utf8);
        mensagem.setText(corpo, utf8);

        Transport.send(mensagem, variavel("SMTP_USER").trim(), variavel("SMTP_PASSWORD"));
    }

    private InternetAddress endereco(String email) throws MessagingException {
        InternetAddress endereco = new InternetAddress(email, true);
        endereco.validate();
        if (endereco.isGroup()) {
            throw new IllegalArgumentException("Informe um único endereço de e-mail.");
        }
        return endereco;
    }

    private String variavel(String nome) {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Configure a variável de ambiente " + nome + ".");
        }
        return valor;
    }
}
