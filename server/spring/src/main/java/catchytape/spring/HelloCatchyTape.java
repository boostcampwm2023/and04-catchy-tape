package catchytape.spring;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HelloCatchyTape {
    @GetMapping("/test")
    public String test() {
        return "hello this is catchy-tape 📼";
    }
}
