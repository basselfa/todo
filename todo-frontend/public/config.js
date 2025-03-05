window.ENV = {
  API_URL: window.location.hostname === 'basselfa.github.io' 
    ? "https://your-backend-api-url.com/api"  // Replace with your actual production API URL
    : "http://localhost:8080/api"
};