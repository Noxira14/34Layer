package com.ibracodeko.layerthirtyfour.api;

import com.ibracodeko.layerthirtyfour.models.MethodResponse;
import com.ibracodeko.layerthirtyfour.models.ScanRequest;
import com.ibracodeko.layerthirtyfour.models.ScanResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    
    // Main scanning endpoint - updated to match new API
    @GET("ddos")
    Call<ScanResponse> performScan(
        @Query("target") String target,
        @Query("time") int time,
        @Query("methods") String methods,
        @Query("key") String key
    );

    // Alternative POST method for scanning (deprecated in new API)
    // @POST("ddos")
    // Call<ScanResponse> performScan(@Body ScanRequest request);

    // Get available methods for a specific feature
    @GET("api/methods")
    Call<MethodResponse> getMethods(@Query("type") String type);

    // Admin endpoints (for real-time method management)
    @POST("api/admin/methods")
    Call<MethodResponse> addMethod(@Body MethodRequest request);

    @GET("api/admin/methods")
    Call<MethodResponse> getAllMethods();

    // Server status check
    @GET("api/status")
    Call<ServerStatusResponse> getServerStatus();

    // User profile endpoints
    @GET("api/user/profile")
    Call<UserProfileResponse> getUserProfile(@Query("userId") String userId);

    // Method request model for admin
    class MethodRequest {
        private String name;
        private String endpoint;
        private String type;
        private String description;

        public MethodRequest(String name, String endpoint, String type, String description) {
            this.name = name;
            this.endpoint = endpoint;
            this.type = type;
            this.description = description;
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    // Server status response model
    class ServerStatusResponse {
        private String status;
        private long timestamp;
        private String version;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }

    // User profile response model
    class UserProfileResponse {
        private String status;
        private UserProfile user;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public UserProfile getUser() { return user; }
        public void setUser(UserProfile user) { this.user = user; }

        public static class UserProfile {
            private String id;
            private String username;
            private String email;
            private String userType;
            private String createdAt;

            // Getters and setters
            public String getId() { return id; }
            public void setId(String id) { this.id = id; }
            
            public String getUsername() { return username; }
            public void setUsername(String username) { this.username = username; }
            
            public String getEmail() { return email; }
            public void setEmail(String email) { this.email = email; }
            
            public String getUserType() { return userType; }
            public void setUserType(String userType) { this.userType = userType; }
            
            public String getCreatedAt() { return createdAt; }
            public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        }
    }
}