package com.perfume.util;

import com.perfume.constant.CheckoutStatus;
import com.perfume.entity.Checkout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


@Component
public class MailUtils {

    @Value("${mail.username}")
    private String APP_EMAIL;

    @Value("${mail.password}")
    private String APP_PASSWORD;

    @Value("${mail.file-content}")
    private String fileNameContent;

    private Properties mailServerProperties;
    private Session getMailSession;
    private MimeMessage mailMessage;

    private String content;

    public MailUtils() {
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
    }

    private String contentFile() {
        try {
            InputStream inputStream = new ClassPathResource(fileNameContent).getInputStream();
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            String data = new String(bdata, StandardCharsets.UTF_8);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "không được mail mẫu ";
    }

    private String setData(Checkout checkout) {
        Map<String, String> map = new HashMap<>();
        map.put("firstname", checkout.getFirstname());
        map.put("lastname", checkout.getLastname());
        map.put("address", checkout.getAddress());
        String status = CheckoutStatus.getCheckoutStatus(checkout.getStatus()).toString();
        map.put("status", status);
        String rs = StringUtils.format(content, map);
        return rs;
    }

    public void send(Checkout checkout) {
        Thread thread = new Thread(() -> {
            try {
                this.sendHtml(checkout);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void sendHtml(Checkout checkout) throws AddressException, MessagingException {
        if(this.content == null){
            this.content = this.contentFile();
        }

        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        mailMessage = new MimeMessage(getMailSession);

        mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(checkout.getEmail()));



        mailMessage.setSubject("Trạng thái đơn hàng - Perfume", "utf-8");

        String emailBody = setData(checkout);
        mailMessage.setContent(emailBody, "text/html; charset=utf-8");

        Transport transport = getMailSession.getTransport("smtp");

        transport.connect("smtp.gmail.com", APP_EMAIL, APP_PASSWORD);
        transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
        transport.close();
    }

    public static void main(String[] args) {

    }


}
