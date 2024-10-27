package com.jacaranda.glamAndGlitter.services;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.jacaranda.glamAndGlitter.model.Cites;
import com.jacaranda.glamAndGlitter.model.User;
import com.jacaranda.glamAndGlitter.respository.CiteRepository;
import com.jacaranda.glamAndGlitter.respository.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailNotificationService {

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    private CiteRepository citeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @PostConstruct
    public void init() {
        start(); // Inicia el programador de notificaciones
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                sendNotifications();
            } catch (UnsupportedEncodingException | MessagingException e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    public void sendNotifications() throws UnsupportedEncodingException, MessagingException {
        List<User> users = userRepository.findAllByEmailNotifications(true);

        for (User user : users) {
            List<Cites> cites = findUpcomingAppointments(user.getId());

            for (Cites cite : cites) {
                boolean oneDayBefore = isOneDayBefore(cite);
                boolean oneHourBefore = isWithinOneHourOrLess(cite);

                if (oneDayBefore || oneHourBefore) {
                    sendEmail(user.getName(), user.getEmail(), cite, oneDayBefore, oneHourBefore);
                }
            }
        }
    }

    private void sendEmail(String name, String email, Cites cite, boolean oneDayBefore, boolean oneHourBefore)
            throws MessagingException, UnsupportedEncodingException {

        String toAddress = email;
        String fromAddress = "a.fraramgar@gmail.com";
        String senderName = "Glam&Glitter";
        String subject = "Cite reminder";
        String content = "Dear [[user]],<br><br>"
                + "Hi!, we remind you that you have an appointment scheduled for " + cite.getDay()
                + " at " + cite.getStartTime() + ".<br>"
                + "The Glam&Glitter Team";

        if (oneDayBefore) {
            subject = "Reminder: Your appointment is tomorrow!";
        } else if (oneHourBefore) {
            subject = "Reminder: Your appointment is in one hour!";
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

	    helper.setFrom(fromAddress, senderName);
	    helper.setTo(toAddress);
	    helper.setSubject(subject);
	    
        content = content.replace("[[user]]", name);
        
        helper.setText(content, true);

        mailSender.send(message);
    }

    private boolean isOneDayBefore(Cites cite) {
        LocalDate today = LocalDate.now();
        LocalDate appointmentDate = cite.getDay().toLocalDate();
        return appointmentDate.isEqual(today.plusDays(1));
    }

    private boolean isWithinOneHourOrLess(Cites cite) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.of(cite.getDay().toLocalDate(), cite.getStartTime().toLocalTime());
        
        // Comprobamos si la cita comienza dentro de la próxima hora
        return now.isBefore(startTime) && now.plusHours(1).isAfter(startTime);
    }

    public List<Cites> findUpcomingAppointments(Integer userId) {
        // Fecha y hora actuales
        LocalDateTime now = LocalDateTime.now();
        LocalDate todayDate = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        // Calcula la fecha de mañana y la hora dentro de una hora
        LocalDate tomorrowDate = todayDate.plusDays(1);
        LocalTime oneHourLater = currentTime.plusHours(1);

        // Realiza la consulta utilizando los parámetros calculados
        return citeRepository.findUpcomingAppointments(userId, tomorrowDate, todayDate, currentTime, oneHourLater);
    }


    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
