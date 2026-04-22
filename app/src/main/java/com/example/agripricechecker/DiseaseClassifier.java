package com.example.agripricechecker;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import org.tensorflow.lite.Interpreter;

public class DiseaseClassifier {
    private Interpreter interpreter;
    private List<String> labelList;
    private int inputSize;
    private final int PIXEL_SIZE = 3;

    public DiseaseClassifier(Context context, String modelPath, String labelPath) throws IOException {
        MappedByteBuffer modelBuffer = loadModelFile(context, modelPath);
        interpreter = new Interpreter(modelBuffer);
        int[] inputShape = interpreter.getInputTensor(0).shape();
        inputSize = inputShape[1];
        labelList = loadLabelList(context, labelPath);
    }

    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        AssetFileDescriptor fd = context.getAssets().openFd(modelPath);
        FileInputStream fis = new FileInputStream(fd.getFileDescriptor());
        FileChannel fc = fis.getChannel();
        return fc.map(FileChannel.MapMode.READ_ONLY, fd.getStartOffset(), fd.getDeclaredLength());
    }

    private List<String> loadLabelList(Context context, String labelPath) throws IOException {
        List<String> labels = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(labelPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            labels.add(line.trim());
        }
        reader.close();
        return labels;
    }

    public String classify(Bitmap bitmap) {
        ByteBuffer buffer = convertBitmapToByteBuffer(bitmap);
        float[][] result = new float[1][labelList.size()];
        interpreter.run(buffer, result);
        return getTopResult(result);
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[inputSize * inputSize];
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true);
        scaled.getPixels(intValues, 0, scaled.getWidth(), 0, 0, scaled.getWidth(), scaled.getHeight());

        byteBuffer.rewind();
        for (int pixelValue : intValues) {
            byteBuffer.putFloat(((pixelValue >> 16) & 0xFF) / 255.0f);
            byteBuffer.putFloat(((pixelValue >> 8) & 0xFF) / 255.0f);
            byteBuffer.putFloat((pixelValue & 0xFF) / 255.0f);
        }
        return byteBuffer;
    }

    private String getTopResult(float[][] result) {
        int maxIdx = -1;
        float maxProb = 0.0f;
        for (int i = 0; i < labelList.size(); i++) {
            if (result[0][i] > maxProb) {
                maxProb = result[0][i];
                maxIdx = i;
            }
        }
        if (maxIdx != -1 && maxProb > 0.15f) {
            return labelList.get(maxIdx) + " (" + String.format("%.1f", maxProb * 100) + "%)";
        }
        return "Not clear/Healthy (Low Confidence)";
    }

    public void close() {
        if (interpreter != null) interpreter.close();
    }
}