# ITEXPay Direct Charge Integration Service

This is a Spring Boot service that integrates with ITEXPay's Direct Card API. It handles the mandatory RSA/AES encryption required for PCI-compliant transactions.

## Prerequisites
- Java 21+
- Maven 3.6+
- ITEXPay Merchant Keys (Public, Private, and Encryption Key)

## Setup

1.  **Configure Credentials**:
    Copy `.env.example` to `.env` and fill in your keys:
    ```bash
    ITEXPAY_PUBLIC_KEY=your_public_key
    ITEXPAY_PRIVATE_KEY=your_private_key
    ITEXPAY_ENCRYPTION_KEY=your_encryption_pub_key
    APP_API_KEY=choose_a_secure_key_for_this_service
    ```

2. [x] **Build the Project**:
    ```bash
    ./mvnw clean install
    ```

3. [x] **Run the Application**:
    ```bash
    ./mvnw spring-boot:run
    ```
    The service will start on port `8081`.

## Testing the API

### 1. Initiate a Direct Charge
You can use the following `curl` command to test the charge initiation. Replace `YOUR_APP_API_KEY` with the `APP_API_KEY` you set in the `.env` file.

```bash
curl -X POST http://localhost:8081/api/v1/charge \
     -H "Content-Type: application/json" \
     -H "X-API-KEY: YOUR_APP_API_KEY" \
     -d '{
    "transaction": {
      "merchantreference": "REF_'$(date +%s)'",
      "redirecturl": "https://yourredirecturl.com",
      "authoption": "3DS",
      "paymentmethod": "card"
    },
    "order": {
      "amount": "100",
      "currency": "NGN",
      "country": "NG"
    },
    "source": {
      "customer": {
        "firstname": "John",
        "lastname": "Doe",
        "email": "john.doe@example.com",
        "msisdn": "08012345678",
        "card": {
          "number": "5111111111111118",
          "expirymonth": "01",
          "expiryyear": "39",
          "cvv": "111"
        },
        "device": {
          "fingerprint": "xyz_fingerprint",
          "ip": "127.0.0.1"
        }
      }
    }
}'
```

### 2. PIN Charge Validation
If you use `authoption: PIN`, use this endpoint to validate the OTP:

```bash
curl -X POST http://localhost:8081/api/v1/charge/validate \
     -H "Content-Type: application/json" \
     -H "X-API-KEY: YOUR_APP_API_KEY" \
     -d '{
        "merchantreference": "YOUR_REF_HERE",
        "otp": "123456"
     }'
```

## Troubleshooting
- **401 Unauthorized**: Check your `X-API-KEY` header and `APP_API_KEY` in `.env`.
- **Encryption Errors**: Ensure the `ITEXPAY_ENCRYPTION_KEY` is the full RSA Public Key string provided by ITEXPay.
- **H2 Console**: Visit `http://localhost:8081/h2-console` (JDBC URL: `jdbc:h2:mem:directchargedb`) to view transaction status.
