// Import CryptoJS library for encryption

//const CryptoJS = require("crypto-js");

// Generate a new key pair
/*const generateKeyPair = () => {
  const privateKey = CryptoJS.lib.WordArray.random(16).toString();
  const publicKey = CryptoJS.SHA256(privateKey).toString();
  return { publicKey, privateKey };
};

// Load private key from localStorage
const loadPrivateKey = () => {
  return localStorage.getItem("privateKey");
};
  
// Load private key from localStorage
const loadPublicKey = () => {
    return localStorage.getItem("publicKey");
};*/


// Generate a new key pair and send the public key to the server
function generateSenderKey () {
  const privateKey = CryptoJS.lib.WordArray.random(16).toString();
  const publicKey = CryptoJS.SHA256(privateKey).toString();

  // Save the private key to localStorage
  localStorage.setItem("senderPrivateKey", privateKey);
  localStorage.setItem("senderPublicKey", publicKey);

  // Make an HTTP request to send the public key to the server
  /*const response = await fetch('https://localhost:5000/home', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, publicKey }),
  });

  if (response.ok) {
    console.log('Public key sent to server successfully!');
  } else {
    console.error('Failed to send public key to server:', response.statusText);
  }*/
};

function generateReceiverKey () {
  const privateKey = CryptoJS.lib.WordArray.random(16).toString();
  const publicKey = CryptoJS.SHA256(privateKey).toString();

  // Save the private key to localStorage
  localStorage.setItem("receiverPrivateKey", privateKey);
  localStorage.setItem("receiverPublicKey", publicKey);

  // Make an HTTP request to send the public key to the server
  /*const response = await fetch('https://localhost:5000/home', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, publicKey }),
  });

  if (response.ok) {
    console.log('Public key sent to server successfully!');
  } else {
    console.error('Failed to send public key to server:', response.statusText);
  }*/
};

// Encrypt a message with recipient's public key
function encrypt (message, recipientPublicKey) {
  const encryptedMessage = CryptoJS.AES.encrypt(message, recipientPublicKey).toString();
  return encryptedMessage;
};

// Decrypt a message with receiver's private key
function decrypt (encryptedMessage, receiverPrivateKey) {
  const decryptedMessage = CryptoJS.AES.decrypt(encryptedMessage, receiverPrivateKey).toString(CryptoJS.enc.Utf8);
  return decryptedMessage;
};

