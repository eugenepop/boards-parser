package nil.webparser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageUnit {

    private Long id;

    private String fileName;

    private int width;

    private int height;

    private int newWidth;

    private int newHeight;

    private float ratio;

    public ImageUnit(Long id, int width, int height, String fileName) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.fileName = fileName;
    }
}
