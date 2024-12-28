// components/myinfo/MyInfo.tsx
import { useEffect } from 'react';
import { RequestHandler } from '../../utils/api/RequestHandler';

function generateRandomString(length: number): string {
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += characters.charAt(Math.floor(Math.random() * characters.length));
  }
  return result;
}

const MyInfoHandler = () => {
  useEffect(() => {
    console.log("triggered");
    const params = new URLSearchParams(location.search);
    const code = params.get("code");
    const verifier = sessionStorage.getItem("verifier");

    if (code && verifier) {
      const postToken = async () => {
        try {
          const response = await RequestHandler.postRequest("/myinfo/token", {
            code,
            verifier
          });
          console.log("Token Response:", response.data);

          const { access_token, dpop_string } = response.data;
          if (access_token && dpop_string) {
            const getPerson = async () => {
              try {
                const personResponse = await RequestHandler.getRequest("/myinfo/person", {
                  access_token,
                  dpop_string
                });
                console.log("Person Response:", personResponse.data);
              } catch (error) {
                console.error("Error fetching person data:", error);
              }
            };

            getPerson();
          } else {
            console.error("Authorization or dpop_string missing in token response");
          }
        } catch (error) {
          console.error("Error fetching token:", error);
        }
      };

      postToken();
    }
  }, [location.search]);

  const handleAuthorize = async () => {
    try {
      const verifier = generateRandomString(10);
      sessionStorage.setItem("verifier", verifier);

      const response = await RequestHandler.getRequest("/myinfo/authorize", { verifier });
      window.location.href = response.data;
    } catch (error) {
      console.error("Error during authorization:", error);
    }
  };

  return (
    <div>
      <h1>MyInfo</h1>
      <div className="card">
        <button onClick={async () => await RequestHandler.getRequest("/myinfo/sandbox/person/S9812381D")}>
          Start MyInfo Sandbox
        </button>
      </div>
      <div className="card">
        <button onClick={handleAuthorize}>
          Start MyInfo
        </button>
      </div>
    </div>
  );
};

export default MyInfoHandler;
