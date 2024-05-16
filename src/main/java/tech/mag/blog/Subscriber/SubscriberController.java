package tech.mag.blog.Subscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @GetMapping("/all")
    public ResponseEntity<List<Subscriber>> getAllSubscribers() {
        List<Subscriber> subscribers = subscriberService.getAllSubscriber();
        return ResponseEntity.ok(subscribers);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribeToBlog(@RequestBody Subscriber subscriber) {
        String response = subscriberService.subscribeOnBlog(subscriber);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/unsubscribe")
    public ResponseEntity<String> updateSubscriptionStatus(@RequestBody Subscriber subscriber) {
        String response = subscriberService.updateSubscriptionStatus(subscriber);
        return ResponseEntity.ok(response);
    }
}
