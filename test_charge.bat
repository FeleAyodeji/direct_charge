@echo off
SET /P API_KEY="Enter your APP_API_KEY: "
SET /P REF="Enter a unique Merchant Reference (e.g. TEST_001): "

curl -X POST http://localhost:8081/api/v1/charge ^
     -H "Content-Type: application/json" ^
     -H "X-API-KEY: %API_KEY%" ^
     -d "{\"transaction\":{\"merchantreference\":\"%REF%\",\"redirecturl\":\"https://yourredirecturl.com\",\"authoption\":\"3DS\",\"paymentmethod\":\"card\"},\"order\":{\"amount\":\"100\",\"currency\":\"NGN\",\"country\":\"NG\"},\"source\":{\"customer\":{\"firstname\":\"John\",\"lastname\":\"Doe\",\"email\":\"john.doe@example.com\",\"msisdn\":\"08012345678\",\"card\":{\"number\":\"5111111111111118\",\"expirymonth\":\"01\",\"expiryyear\":\"39\",\"cvv\":\"111\"},\"device\":{\"fingerprint\":\"xyz_fingerprint\",\"ip\":\"127.0.0.1\"}}}}"

pause
