package org.jobrunr.example.email;

import org.jobrunr.example.employee.Employee;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.file.Path;

@Component
public class EmailService {

    public JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendSalarySlip(Employee employee, Path salarySlipPath) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(employee.getEmail());
        helper.setSubject("Your weekly salary slip");
        helper.setText(String.format("Dear %s,\n\nhere you can find your weekly salary slip. \n \nThanks again for your hard work,\n Acme corp", employee.getFirstName()));

        FileSystemResource file = new FileSystemResource(salarySlipPath);
        helper.addAttachment("Salary Slip", file);
        //emailSender.send(message); commented as we would otherwise send mails

    }

}
