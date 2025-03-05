window.ENV = {
  API_URL: window.location.hostname === 'basselfa.github.io' 
    ? "https://todo-api.basselfa.com/api"  // Production API URL - update this with your actual backend URL
    : "http://localhost:8080/api"  // Local development API URL
};;