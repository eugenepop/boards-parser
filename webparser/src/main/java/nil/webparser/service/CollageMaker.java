package nil.webparser.service;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import nil.webparser.dto.ImageUnit;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CollageMaker {

    private static final int MAX_IMAGES_NUMBER = 5;
    private static final int FIRST_ROW_MAX_COLUMN_NUM = 2;
    private static final int SECOND_ROW_MAX_COLUMN_NUM = 3;
    private static final int MAX_ROW_HEIGHT = 400;
    private static final int MAX_WHOLE_WIDTH = 500;
    private static final int MAX_WHOLE_HEIGHT = 500;
    private static final int BORDER_WIDTH = 5;

    //public void init() {}

    private List<ImageUnit> sortByRatio(List<ImageUnit> imageUnits) {
        imageUnits.forEach(it -> it.setRatio((float) it.getWidth() / it.getHeight()));
        return imageUnits.stream()
                .sorted((d1, d2) -> Double.compare(d2.getRatio(), d1.getRatio()))
                .collect(Collectors.toList());
    }

    public List<ImageUnit> transform(List<ImageUnit> images) {

        List<ImageUnit> imageUnits = sortByRatio(images);

        if (imageUnits.size() == 1) {
            int newWholeWidth = MAX_WHOLE_WIDTH - BORDER_WIDTH;

            float ratio_0 = imageUnits.get(0).getRatio();

            int newHeight_0 = Math.round(newWholeWidth / ratio_0);

            if(newHeight_0 > MAX_ROW_HEIGHT){
                newHeight_0 = MAX_ROW_HEIGHT;
            }

            imageUnits.get(0).setNewWidth(newWholeWidth);
            imageUnits.get(0).setNewHeight(newHeight_0);
        }

        if (imageUnits.size() >= FIRST_ROW_MAX_COLUMN_NUM) {

            int firstRowWholeWidth = MAX_WHOLE_WIDTH - (FIRST_ROW_MAX_COLUMN_NUM - 1) * BORDER_WIDTH;

            float ratio_0 = imageUnits.get(0).getRatio();
            float ratio_1 = imageUnits.get(1).getRatio();

            int widthSum = imageUnits.get(0).getWidth() + imageUnits.get(1).getWidth();

            float widthPortion_0 = (float) imageUnits.get(0).getWidth() / widthSum;
            float widthPortion_1 = (float) imageUnits.get(1).getWidth() / widthSum;

            int newWidth_0 = Math.round(firstRowWholeWidth * widthPortion_0);
            int newHeight_0 = Math.round(newWidth_0 / ratio_0);

            int newWidth_1 = Math.round(firstRowWholeWidth * widthPortion_1);
            int newHeight_1 = Math.round(newWidth_1 / ratio_1);

            //round-produced redundant pixels
            int firstRowDiffWeight = firstRowWholeWidth - newWidth_0 - newWidth_1;
            if (Math.abs(firstRowDiffWeight) > 0) {
                newWidth_0 += firstRowDiffWeight;
            }

            //max height threshold
            if (newHeight_0 > MAX_ROW_HEIGHT) {
                newHeight_0 = MAX_ROW_HEIGHT;
            }
            if (newHeight_1 > MAX_ROW_HEIGHT) {
                newHeight_1 = MAX_ROW_HEIGHT;
            }
            //scale height min to max
            int maxNewHeight = NumberUtils.max(newHeight_0, newHeight_1);
            newHeight_0 = maxNewHeight;
            newHeight_1 = maxNewHeight;

            imageUnits.get(0).setNewWidth(newWidth_0);
            imageUnits.get(0).setNewHeight(newHeight_0);

            imageUnits.get(1).setNewWidth(newWidth_1);
            imageUnits.get(1).setNewHeight(newHeight_1);

            if (imageUnits.size() > FIRST_ROW_MAX_COLUMN_NUM) {

                int imagesLeftNumber = imageUnits.size() - FIRST_ROW_MAX_COLUMN_NUM;
                int borderCompensation = (imagesLeftNumber - 1) * BORDER_WIDTH;
                int secondWithWholeWidth = MAX_WHOLE_WIDTH - borderCompensation;
                int secondRowNewWidth = Math.round((float) secondWithWholeWidth / imagesLeftNumber);

                int secondRowMaxHeight = Integer.MIN_VALUE;
                int secondRowMinHeight = Integer.MAX_VALUE;
                for (int i = 2; i < imageUnits.size(); i++) {
                    imageUnits.get(i).setNewWidth(secondRowNewWidth);
                    imageUnits.get(i).setNewHeight(Math.round(secondRowNewWidth / imageUnits.get(i).getRatio()));
                    //max height threshold
                    if (newHeight_0 > MAX_ROW_HEIGHT) {
                        imageUnits.get(i).setNewHeight(MAX_ROW_HEIGHT);
                    }
                    if (imageUnits.get(i).getNewHeight() > secondRowMaxHeight) {
                        secondRowMaxHeight = imageUnits.get(i).getNewHeight();
                    }
                    if (imageUnits.get(i).getNewHeight() < secondRowMinHeight) {
                        secondRowMinHeight = imageUnits.get(i).getNewHeight();
                    }
                }
                //round-produced redundant pixels
                int diffWeight = secondWithWholeWidth - secondRowNewWidth * imagesLeftNumber;
                if (Math.abs(diffWeight) > 0) {
                    imageUnits.get(2).setNewWidth(imageUnits.get(2).getNewWidth() + diffWeight);
                }
                //scale height min to max
                //not just min or max but based on majority
                for (int i = 2; i < imageUnits.size(); i++) {
                    imageUnits.get(i).setNewHeight(secondRowMinHeight);
                }
            }
        }

        return imageUnits;
    }

}
