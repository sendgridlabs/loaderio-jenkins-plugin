package io.loader.jenkins.api;

import net.sf.json.JSONObject;
import net.sf.json.JSON;

public class TestData {
    public String testId;
    public String status;
    public int duration;

    public TestData(JSONObject json) {
        testId = json.getString("test_id");
        status = json.getString("status");
        duration = json.getInt("duration");
    }

    public String toString() {
        return String.format("#<TestData testId: %s, status: %s, duration: %d>", testId, status, duration);
    }
}
