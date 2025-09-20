package com.ibracodeko.layerthirtyfour.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.ibracodeko.layerthirtyfour.R;
import com.ibracodeko.layerthirtyfour.models.ScanResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ResultDisplayHelper {

    public static void showWebMultiPortingResult(Context context, View resultContainer, ScanResponse result) {
        LinearLayout resultsLayout = resultContainer.findViewById(R.id.resultsLayout);
        if (resultsLayout == null) {
            createResultsLayout(context, resultContainer);
            resultsLayout = resultContainer.findViewById(R.id.resultsLayout);
        }
        
        resultsLayout.removeAllViews();
        
        // Add result items
        addResultItem(context, resultsLayout, "Status", result.getStatus());
        addResultItem(context, resultsLayout, "Message", result.getMessage());
        addResultItem(context, resultsLayout, "Execution Time", result.getExecutionTime());
        
        if (result.getData() != null) {
            ScanResponse.ScanData data = result.getData();
            addResultItem(context, resultsLayout, "Target", data.getTarget());
            addResultItem(context, resultsLayout, "Method", data.getMethod());
            addResultItem(context, resultsLayout, "Duration", String.valueOf(data.getDuration()) + "s");
            
            if (data.getResults() != null) {
                for (Map.Entry<String, Object> entry : data.getResults().entrySet()) {
                    addResultItem(context, resultsLayout, entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            
            if (data.getErrors() != null && !data.getErrors().isEmpty()) {
                addResultSection(context, resultsLayout, "Errors");
                for (String error : data.getErrors()) {
                    addResultItem(context, resultsLayout, "Error", error, true);
                }
            }
        }
    }

    public static void showIPTroubleshootingResult(Context context, View resultContainer, ScanResponse result) {
        LinearLayout resultsLayout = resultContainer.findViewById(R.id.resultsLayout);
        if (resultsLayout == null) {
            createResultsLayout(context, resultContainer);
            resultsLayout = resultContainer.findViewById(R.id.resultsLayout);
        }
        
        resultsLayout.removeAllViews();
        
        addResultSection(context, resultsLayout, "IP Troubleshooting Results");
        addResultItem(context, resultsLayout, "Status", result.getStatus());
        addResultItem(context, resultsLayout, "Timestamp", new Date(result.getTimestamp()).toString());
        
        if (result.getData() != null) {
            ScanResponse.ScanData data = result.getData();
            addResultItem(context, resultsLayout, "Target IP/Domain", data.getTarget());
            addResultItem(context, resultsLayout, "Test Method", data.getMethod());
            addResultItem(context, resultsLayout, "Test Duration", data.getDuration() + " seconds");
            
            // IP-specific results
            if (data.getResults() != null) {
                addResultSection(context, resultsLayout, "Network Analysis");
                for (Map.Entry<String, Object> entry : data.getResults().entrySet()) {
                    String key = entry.getKey();
                    String value = String.valueOf(entry.getValue());
                    
                    // Format IP-specific results
                    if (key.equalsIgnoreCase("ping_result")) {
                        addResultItem(context, resultsLayout, "Ping Test", value);
                    } else if (key.equalsIgnoreCase("traceroute")) {
                        addResultItem(context, resultsLayout, "Trace Route", value);
                    } else if (key.equalsIgnoreCase("dns_lookup")) {
                        addResultItem(context, resultsLayout, "DNS Lookup", value);
                    } else if (key.equalsIgnoreCase("port_scan")) {
                        addResultItem(context, resultsLayout, "Port Scan", value);
                    } else {
                        addResultItem(context, resultsLayout, key, value);
                    }
                }
            }
            
            if (data.getMetadata() != null) {
                addResultSection(context, resultsLayout, "Additional Info");
                for (Map.Entry<String, String> entry : data.getMetadata().entrySet()) {
                    addResultItem(context, resultsLayout, entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public static void showProxyServerResult(Context context, View resultContainer, ScanResponse result) {
        LinearLayout resultsLayout = resultContainer.findViewById(R.id.resultsLayout);
        if (resultsLayout == null) {
            createResultsLayout(context, resultContainer);
            resultsLayout = resultContainer.findViewById(R.id.resultsLayout);
        }
        
        resultsLayout.removeAllViews();
        
        addResultSection(context, resultsLayout, "Proxy Server Test Results");
        addResultItem(context, resultsLayout, "Status", result.getStatus());
        addResultItem(context, resultsLayout, "Test Time", new Date(result.getTimestamp()).toString());
        
        if (result.getData() != null) {
            ScanResponse.ScanData data = result.getData();
            addResultItem(context, resultsLayout, "Target Domain", data.getTarget());
            addResultItem(context, resultsLayout, "Test Method", data.getMethod());
            addResultItem(context, resultsLayout, "Duration", data.getDuration() + " seconds");
            
            // Proxy-specific results
            if (data.getResults() != null) {
                addResultSection(context, resultsLayout, "Proxy Analysis");
                for (Map.Entry<String, Object> entry : data.getResults().entrySet()) {
                    String key = entry.getKey();
                    String value = String.valueOf(entry.getValue());
                    
                    // Format proxy-specific results
                    if (key.equalsIgnoreCase("proxy_status")) {
                        addResultItem(context, resultsLayout, "Proxy Status", value);
                    } else if (key.equalsIgnoreCase("proxy_speed")) {
                        addResultItem(context, resultsLayout, "Proxy Speed", value + " ms");
                    } else if (key.equalsIgnoreCase("anonymity_level")) {
                        addResultItem(context, resultsLayout, "Anonymity Level", value);
                    } else if (key.equalsIgnoreCase("country")) {
                        addResultItem(context, resultsLayout, "Proxy Location", value);
                    } else {
                        addResultItem(context, resultsLayout, key, value);
                    }
                }
            }
        }
    }

    private static void createResultsLayout(Context context, View resultContainer) {
        LinearLayout resultsLayout = new LinearLayout(context);
        resultsLayout.setId(R.id.resultsLayout);
        resultsLayout.setOrientation(LinearLayout.VERTICAL);
        resultsLayout.setPadding(16, 16, 16, 16);
        
        if (resultContainer instanceof LinearLayout) {
            ((LinearLayout) resultContainer).addView(resultsLayout);
        }
    }

    private static void addResultSection(Context context, LinearLayout parent, String sectionTitle) {
        TextView sectionView = new TextView(context);
        sectionView.setText(sectionTitle);
        sectionView.setTextSize(18);
        sectionView.setTextColor(context.getColor(R.color.primary_color));
        sectionView.setPadding(0, 24, 0, 8);
        sectionView.setTypeface(null, android.graphics.Typeface.BOLD);
        parent.addView(sectionView);
    }

    private static void addResultItem(Context context, LinearLayout parent, String label, String value) {
        addResultItem(context, parent, label, value, false);
    }

    private static void addResultItem(Context context, LinearLayout parent, String label, String value, boolean isError) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_result, parent, false);
        
        TextView tvLabel = itemView.findViewById(R.id.tvResultLabel);
        TextView tvValue = itemView.findViewById(R.id.tvResultValue);
        
        tvLabel.setText(label + ":");
        tvValue.setText(value);
        
        if (isError) {
            tvValue.setTextColor(context.getColor(R.color.red_500));
        }
        
        parent.addView(itemView);
    }

    public static void exportScanResult(Context context, ScanResponse result, String testType) {
        try {
            // Create filename with timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String timestamp = dateFormat.format(new Date());
            String filename = "34Layer_" + testType + "_" + timestamp + ".txt";
            
            // Get external storage directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, filename);
            
            // Create file content
            StringBuilder content = new StringBuilder();
            content.append("=== 34Layer Scan Results ===\n");
            content.append("Test Type: ").append(testType).append("\n");
            content.append("Export Time: ").append(new Date().toString()).append("\n");
            content.append("Status: ").append(result.getStatus()).append("\n");
            content.append("Message: ").append(result.getMessage()).append("\n");
            content.append("Execution Time: ").append(result.getExecutionTime()).append("\n");
            content.append("\n=== Scan Data ===\n");
            
            if (result.getData() != null) {
                ScanResponse.ScanData data = result.getData();
                content.append("Target: ").append(data.getTarget()).append("\n");
                content.append("Method: ").append(data.getMethod()).append("\n");
                content.append("Duration: ").append(data.getDuration()).append(" seconds\n");
                
                if (data.getResults() != null) {
                    content.append("\n=== Results ===\n");
                    for (Map.Entry<String, Object> entry : data.getResults().entrySet()) {
                        content.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                    }
                }
                
                if (data.getErrors() != null && !data.getErrors().isEmpty()) {
                    content.append("\n=== Errors ===\n");
                    for (String error : data.getErrors()) {
                        content.append("- ").append(error).append("\n");
                    }
                }
                
                if (data.getMetadata() != null) {
                    content.append("\n=== Metadata ===\n");
                    for (Map.Entry<String, String> entry : data.getMetadata().entrySet()) {
                        content.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                    }
                }
            }
            
            content.append("\n=== Generated by 34Layer Android App ===\n");
            content.append("Created by Ibra Decode & Komodigi Project's\n");
            
            // Write to file
            FileWriter writer = new FileWriter(file);
            writer.write(content.toString());
            writer.close();
            
            // Share file
            Uri fileUri = FileProvider.getUriForFile(context, 
                context.getPackageName() + ".fileprovider", file);
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "34Layer Scan Results - " + testType);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            context.startActivity(Intent.createChooser(shareIntent, "Export Scan Results"));
            
            Toast.makeText(context, "Results exported to: " + filename, Toast.LENGTH_LONG).show();
            
        } catch (IOException e) {
            Toast.makeText(context, "Failed to export results: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }
}