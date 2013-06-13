package io.loader.jenkins.api;

import net.sf.json.JSONObject;

public class SummaryData {
    public int    avgResponseTime;
    public double avgErrorRate;

    public SummaryData(JSONObject json) {
        avgResponseTime = json.getInt("avg_response_time");
        avgErrorRate    = json.getDouble("avg_error_rate");
    }

    public String toString() {
        return String.format("#<SummaryData avgResponseTime: %d, avgErrorRate: %f>", avgResponseTime, avgErrorRate);
    }
}
