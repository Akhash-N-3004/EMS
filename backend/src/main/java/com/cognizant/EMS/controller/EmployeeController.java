package com.cognizant.EMS.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.EMS.Exception.ResourceNotFoundException;
import com.cognizant.EMS.entity.Employee;
import com.cognizant.EMS.service.AdminService;
import com.cognizant.EMS.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;

class LoginForm {
  private String emailId;
  private String password;

  public String getEmailId() {
    return emailId;
  }

  public String getPassword() {
    return password;
  }

  public void setEmailId(String email) {
    this.emailId = email;
  }

  public void setPassword(String pass) {
    this.password = pass;
  }

  @Override
  public String toString() {
    return "Email :" + emailId + "\n" + "Password:" + password;
  }
}

@RestController
@CrossOrigin("*")
@RequestMapping("/employees")
@Slf4j
public class EmployeeController {
  @Autowired
  private final EmployeeService employeeService;

  @Autowired
  private final AdminService adminService;

  public EmployeeController(EmployeeService employeeService, AdminService adminService) {
    this.employeeService = employeeService;
    this.adminService = adminService;
  }

  @GetMapping
  public ResponseEntity<List<Employee>> getAllEmployees() {
    List<Employee> employees = employeeService.getAllEmployees();
    log.info("Successfully fetched all the employee details");
    return ResponseEntity.ok(employees);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") Long id) throws ResourceNotFoundException {
    Employee employee = employeeService.getEmployee(id);
    if (employee != null) {
    	log.info("Successfully fetched the employee details");
      return ResponseEntity.ok(employee);

    } else {
      throw new ResourceNotFoundException("Employee not found");
    }

  }

  @PostMapping
  public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
    Employee createdEmployee = employeeService.createEmployee(employee);
    log.info("Successfully fetched all the employee details");
    return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Employee> updateEmployee(@PathVariable("id") Long id, @RequestBody Employee updatedEmployee)
      throws ResourceNotFoundException {
    Employee employee = employeeService.updateEmployee(id, updatedEmployee);

    if (employee != null) {
    	log.info("Successfully updated the employee data");
    	return ResponseEntity.ok(employee);
    } else {
      throw new ResourceNotFoundException("There is employee data in this id");
    }

  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
    employeeService.deleteEmployee(id);
    log.info("Succesfully deleted the data");
    return ResponseEntity.noContent().build();
  }

//  @PostMapping("/{id}/request-training-slot")
//  public ResponseEntity<Void> requestTrainingSlot(@PathVariable("id") Long id) {
//    employeeService.requestTrainingSlot(id);
//    return ResponseEntity.ok().build();
//  }
//
//  @PostMapping("/{id}/request-certificate")
//  public ResponseEntity<Void> requestCertificate(@PathVariable("id") Long id,
//      @RequestParam("certificateType") String certificateType) {
//    employeeService.requestCertificate(id, certificateType);
//    return ResponseEntity.ok().build();
//  }

  @PostMapping("/login")
  public ResponseEntity<Employee> login(@RequestBody LoginForm login) {
    if (login.getEmailId() == null || login.getPassword() == null) {
      return ResponseEntity.status(418).body(null);
    }

    Employee emp = employeeService.login(login.getEmailId(), login.getPassword());
    if (emp != null) {
      return ResponseEntity.ok(emp);
    }

    return ResponseEntity.status(418).body(null);
  }
  
  @PostMapping("/employee/check-password")
  public boolean checkPassword(@RequestParam String emailId, @RequestParam String password) {
      Employee employee = employeeService.getEmployeeByEmailId(emailId);

      if (employee != null && employee.getPassword().equals(password)) {
          Employee emp = employeeService.getEmployeeByEmailId(emailId);
          emp.setPassword(password);
          employeeService.save(emp);
          return true;
      }

      return false;
  }
  
  @PostMapping("/{id}/certificate")
  public ResponseEntity<String> generateCertificate(@PathVariable("id") Long employeeId) {
      
      Employee employee = employeeService.getEmployee(employeeId);

      
      if (employee == null) {
          return ResponseEntity.notFound().build();
      }

      
      LocalDate currentDate = LocalDate.now();

      
      LocalDate joinDate = LocalDate.parse(employee.getJoinDate());

      
      long experience = ChronoUnit.YEARS.between(joinDate, currentDate);

      
      String certificateMessage = "This employee "  +employee.getLastName() + " has an experience of " + experience + " years in our organization.";


      
      String fileName = "certificate_"+  employee.getId()+  ".pdf";
      generatePDF(certificateMessage, fileName);

      
      sendCertificateByEmail(employee.getEmailId(), fileName);

      return ResponseEntity.ok("Certificate generated and sent successfully");
  }
  
  private void generatePDF(String certificateMessage, String fileName) {
      
      try {
          PDDocument document = new PDDocument();
          PDPage page = new PDPage();
          document.addPage(page);

          PDPageContentStream contentStream = new PDPageContentStream(document, page);
          contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
          contentStream.beginText();
          contentStream.newLineAtOffset(100, 700);
          contentStream.showText(certificateMessage);		
          contentStream.endText();
          contentStream.close();

          document.save(fileName);
          document.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
  
  
  private JavaMailSender mailSsender;
  public JavaMailSender getJavaMailSender()
	{
  	JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost("smtp.gmail.com");
	    mailSender.setPort(25);

	    mailSender.setUsername("admin@gmail.com");
	    mailSender.setPassword("password");

	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");

	    return mailSender;
	}
  
  
  public void sendMailWithAttachment(String to, String subject, String body, String fileToAttach)
  {
  	MimeMessagePreparator preparator = new MimeMessagePreparator()
  	{
          public void prepare(MimeMessage mimeMessage) throws Exception
          {
              mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
              mimeMessage.setFrom(new InternetAddress("admin@gmail.com"));
              mimeMessage.setSubject(subject);
              mimeMessage.setText(body);

              FileSystemResource file = new FileSystemResource(new File(fileToAttach));
              MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
              helper.addAttachment("logo.jpg", file);
          }
      };

      try {
          // mailSender.send(preparator);
      }
      catch (MailException ex) {
          // simply log it and go on...
          System.err.println(ex.getMessage());
      }
  }
  
  private void sendCertificateByEmail(String recipientEmail, String fileName) {
      // Send the certificate as an email attachment using a mail library
      // Implement the logic to send the email with the certificate attachment
      // Here's an example using JavaMail

      // Set up your email properties and authentication

      Properties properties = new Properties();
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.host", "smtp.gmail.com"); // Replace with your SMTP server
      properties.put("mail.smtp.port", "465"); // Replace with your SMTP server port

      
//      # SMTP server host
//      spring.mail.host=smtp.example.com
//      # SMTP server port
//      spring.mail.port=587
//      # Username and password for authentication
//      spring.mail.username=your-email@example.com
//      spring.mail.password=your-password
//      # SMTP server protocol
//      spring.mail.protocol=smtp
//      # Additional properties for JavaMail session
//      spring.mail.properties.mail.smtp.auth=true
//      spring.mail.properties.mail.smtp.starttls.enable=true
      
      
      String senderEmail = "noreply.ems.cts@gmail.com"; // Replace with your email address
      String senderPassword = "ABcd#@1234"; // Replace with your email password

      javax.mail.Session session = javax.mail.Session.getInstance(properties, new javax.mail.Authenticator() {
          protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
              return new javax.mail.PasswordAuthentication(senderEmail, senderPassword);
          }
      });

      try {
javax.mail.Message message = new MimeMessage(session);
           message.setFrom(new InternetAddress(senderEmail));
          message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
          message.setSubject("Certificate");
          message.setText("Please find attached the certificate.");

          MimeBodyPart messageBodyPart = new MimeBodyPart();
          Multipart multipart = new MimeMultipart();

          messageBodyPart.setText("Please find attached the certificate.");
          multipart.addBodyPart(messageBodyPart);

          MimeBodyPart attachmentBodyPart = new MimeBodyPart();
          attachmentBodyPart.attachFile(new File(fileName));
          multipart.addBodyPart(attachmentBodyPart);

          message.setContent(multipart);

          javax.mail.Transport.send(message);

          // Delete the generated PDF file after sending the email
          File file = new File(fileName);
          if (file.delete()) {
              System.out.println("Certificate file deleted successfully.");
          } else {
              System.out.println("Failed to delete certificate file.");
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
  
  }
}
