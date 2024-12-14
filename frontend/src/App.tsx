import { useEffect, useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import { RequestHandler } from './utils/api/RequestHandler'
import axios from 'axios'


function generateRandomString(length: number): string {
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
      result += characters.charAt(Math.floor(Math.random() * characters.length));
  }
  return result;
}

function App() {
  useEffect(() => {
    console.log("triggered")
    // Extract the "code" parameter from the query string
    const params = new URLSearchParams(location.search);
    const code = params.get("code");
    const verifier = sessionStorage.getItem("verifier");
  
    if (code && verifier) {
      // Make the POST request to your backend
      console.log("triggered12")
      const postToken = async () => {
        try {
          const response = await RequestHandler.postRequest("/myinfo/token", {
            code,
            verifier
          });
          console.log("Token Response:", response.data);
  
          // Extract authorization and dpop_string from the response
          const { access_token, dpop_string } = response.data;

                      // Make the GET request to /myinfo/person using the authorization and dpop_string

          if (access_token && dpop_string) {
            const getPerson = async () => {
              try {
                const personResponse = await RequestHandler.getRequest("/myinfo/person", {
                  access_token,
                  dpop_string
                });
                console.log("Person Response:", personResponse.data);
                // Handle the response (e.g., store data, navigate, show a message)
              } catch (error) {
                console.error("Error fetching person data:", error);
                // Handle error (e.g., show an error message)
              }
            };
  
            getPerson();
          } else {
            console.error("Authorization or dpop_string missing in token response");
          }
        } catch (error) {
          console.error("Error fetching token:", error);
          // Handle error (e.g., show an error message)
        }
      };
  
      postToken();
    }
  }, [location.search]);  

  const handleAuthorize = async () => {
    try {
      // Step 1: Generate and store the random string in sessionStorage
      const verifier = generateRandomString(10);
      sessionStorage.setItem("verifier", verifier);

      // Step 2: Call the /myinfo/authorize endpoint
      const response = await RequestHandler.getRequest("/myinfo/authorize", { verifier });

      // Redirect the user to the returned URL
      window.location.href = response.data;
    } catch (error) {
      console.error("Error during authorization:", error);
    }
  };

  return (
    <>

      <h1>Vite + React</h1>
      

      <div className="card">
        <button onClick={async () => await RequestHandler.getRequest("/myinfo/sandbox/person/S9812381D")}>
          Start myinfo sandbox
        </button>
      </div>

      <div className="card">
        <button onClick={handleAuthorize}>
          Start myinfo
        </button>
      </div>

      <div className="card">
        <button onClick={async () => await RequestHandler.getRequest("/health")}>
          Start health
        </button>
      </div>
      <p className="read-the-docs">
        Click on the Vite and React logos to learn more
      </p>
    </>
  )
}

export default App
