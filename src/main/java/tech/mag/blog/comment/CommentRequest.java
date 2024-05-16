package tech.mag.blog.comment;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private String text;
    private UUID blogId;
}
