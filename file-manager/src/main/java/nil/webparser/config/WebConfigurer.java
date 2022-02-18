//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@EnableWebMvc
//@ComponentScan
//public class WebConfigurer implements WebMvcConfigurer {
//
//    @Value("${upload.image.directory}")
//    private String IMAGE_DESTINATION_FOLDER;
//
//    private String uploadDirectory = System.getProperty("user.home") + IMAGE_DESTINATION_FOLDER;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler(uploadDirectory + "/**")
//                .addResourceLocations("file:" + uploadDirectory + "/");
//    }
//}
