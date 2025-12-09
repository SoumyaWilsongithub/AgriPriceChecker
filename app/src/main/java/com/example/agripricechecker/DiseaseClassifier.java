package com.example.agripricechecker;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class DiseaseClassifier {

    private static final String TAG = "DiseaseClassifier";
    private static final String MODEL_FILE = "crop_disease.tflite";
    private static final String LABEL_FILE = "labels.txt";

    // --- Configuration based on typical MobileNetV2/EfficientNet Models ---
    private static final int INPUT_SIZE = 224; // Check your model's expected input size
    // Assuming the model is non-quantized (FLOAT32) and requires normalization.
    // If your model is QUANTIZED, change this to UINT8 and remove the NormalizeOp.
    private static final DataType INPUT_DATA_TYPE = DataType.FLOAT32;

    // Output data type is usually FLOAT32
    private static final DataType OUTPUT_DATA_TYPE = DataType.FLOAT32;

    private Interpreter tflite;
    private List<String> labels;
    private ImageProcessor imageProcessor;
    private TensorBuffer outputBuffer;

    public DiseaseClassifier(Context context) throws IOException {
        // 1. Load Labels
        labels = loadLabelList(context);
        int numClasses = labels.size();

        // 2. Load Model and Interpreter
        try {
            ByteBuffer modelBuffer = FileUtil.loadMappedFile(context, MODEL_FILE);
            tflite = new Interpreter(modelBuffer, new Interpreter.Options());
        } catch (Exception e) {
            // CRITICAL: Log the error if the model fails to load
            Log.e(TAG, "FATAL: Failed to initialize TFLite model from assets/" + MODEL_FILE, e);
            throw new IOException("Failed to initialize TFLite: " + e.getMessage(), e);
        }

        // 3. Setup Input and Output Structures
        // Input: [1, INPUT_SIZE, INPUT_SIZE, 3]
        // Output: [1, numClasses] (Adjust based on your model's output)
        outputBuffer = TensorBuffer.createFixedSize(new int[]{1, numClasses}, OUTPUT_DATA_TYPE);

        // 4. Setup Image Processor (Preprocessing)
        imageProcessor = new ImageProcessor.Builder()
                // Resize the image to match the model's expected input size
                .add(new ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeMethod.BILINEAR))
                // CRITICAL FOR FLOAT MODELS: Normalize the pixel values from 0-255 to 0.0-1.0
                // If your model is UINT8 (quantized), you MUST remove this line.
                // .add(new NormalizeOp(0f, 255f)) // Example: For MobileNetV2, 0 mean, 255 std dev
                .build();
    }

    // --- Classification Method ---

    public String classify(Bitmap inputImage) {
        if (tflite == null) {
            return "Error: Model not initialized.";
        }

        // 1. Convert Bitmap to TensorImage
        TensorImage tensorImage = new TensorImage(INPUT_DATA_TYPE);
        tensorImage.load(inputImage);

        // 2. Preprocess the image using the processor
        TensorImage processedImage = imageProcessor.process(tensorImage);

        // 3. Run Inference
        try {
            tflite.run(processedImage.getBuffer(), outputBuffer.getBuffer());
        } catch (Exception e) {
            // CRITICAL: Log the error if inference fails (usually due to shape/type mismatch)
            Log.e(TAG, "FATAL: TFLite inference failed! Check input/output buffers.", e);
            return "Classification Failed: Check Logcat for details (Tag: DiseaseClassifier)";
        }


        // 4. Process Output (Post-processing)
        TensorProcessor probabilityProcessor = new TensorProcessor.Builder().build();
        float[] probabilities = probabilityProcessor.process(outputBuffer).getFloatArray();

        // 5. Find the top result
        PriorityQueue<Result> topK = new PriorityQueue<>(1, (r1, r2) -> Float.compare(r2.confidence, r1.confidence));

        for (int i = 0; i < probabilities.length; i++) {
            topK.add(new Result(labels.get(i), probabilities[i]));
        }

        Result topResult = topK.poll();
        if (topResult != null) {
            return String.format("%s (%.2f%%)", topResult.label, topResult.confidence * 100);
        } else {
            return "No classification result found.";
        }
    }

    // --- Helper Methods ---

    private List<String> loadLabelList(Context context) throws IOException {
        List<String> labelList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(LABEL_FILE)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                labelList.add(line);
            }
        }
        return labelList;
    }

    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }

    // --- Simple Result Class ---
    private static class Result {
        public final String label;
        public final float confidence;

        public Result(String label, float confidence) {
            this.label = label;
            this.confidence = confidence;
        }
    }
}