# ITEXPay Direct Charge Documentation

## Overview
Direct Charge allows merchants who are PCI DSS compliant to send encrypted card data to the gateway for card charge transactions.

---

## Authentication
ITEXPay Gateway Direct Card API uses public, private, and encrypted public keys issued to your merchant account to authenticate requests.

- Authentication is done via:
  Authorization: Bearer <access_token>
- All API requests must be made over HTTPS

---

## Token Generation

### Endpoint
POST https://staging.itexpay.com/api/v1/direct/transaction/authenticate

### Request Body
{
  "publickey": "your_public_key",
  "privatekey": "your_private_key"
}

### Response
{
  "access_token": "token_here",
  "expires_in": 1800,
  "code": "00",
  "message": "approved"
}

---

## Charge Initiation

### 3DS Authentication
- User is redirected to bank/card network page
- Completes authentication (OTP, etc.)
- Redirected back after payment

### PIN Authentication
- User enters PIN
- OTP is sent
- OTP must be validated

---

## Encryption

Before sending payment data, you must encrypt it using:
- AES (for data)
- RSA (for key)

---

## Payment Payload (Before Encryption)

{
  "transaction": {
    "merchantreference": "M1603t798eg9d106n10m_y22",
    "callbackurl": "https://yourcallbackurl.com",
    "redirecturl": "https://yourredirecturl.com",
    "authoption": "3DS",
    "paymentmethod": "card"
  },
  "order": {
    "amount": "10",
    "description": "test",
    "currency": "NGN",
    "country": "NG"
  }
}

---

## Charge Request

POST https://staging.itexpay.com/api/v1/direct/transaction/charge

Headers:
Authorization: Bearer <access_token>

Body:
{
  "ctx": "encrypted_key",
  "data": "encrypted_payload"
}

---

## Charge Response

{
  "transaction": {
    "reference": "IBK_930867601653597010708",
    "merchantreference": "M1603t798eg9d106n10m_y22",
    "redirecturl": "https://bank-auth-url"
  }
}

---

## PIN Charge Validation

POST https://staging.itexpay.com/api/v1/direct/transaction/charge/validate

Body:
{
  "ctx": "encrypted_key",
  "data": "encrypted_payload_with_otp"
}

---

## Validation Response

{
  "transaction": {
    "reference": "IBK_455185091653597127336",
    "merchantreference": "M1E603t798ewg9e0m_y32"
  },
  "code": "00",
  "message": "approved"
}

---

## Summary Flow

1. Generate access token  
2. Build transaction payload  
3. Encrypt payload  
4. Send charge request  
5. Handle 3DS or PIN flow  
6. Validate OTP (if PIN)  
7. Check transaction status  
