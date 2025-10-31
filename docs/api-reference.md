## API Reference

### Auth
- POST `/auth/login` { username, password, tenant } → { accessToken }
- POST `/auth/register` → 201
- POST `/auth/forgot` → 202
- POST `/auth/reset` → 200

### Invoices
- POST `/invoices` → create draft
- POST `/invoices/{id}/sign` → sign
- POST `/invoices/{id}/send` { destinationUrn, note }
- POST `/invoices/{id}/update` { destinationUrn, note }
- POST `/invoices/{id}/cancel` { reason, date }
- GET `/invoices/{id}/pdf`

### Query (e-Fatura)
- GET `/provider/query/outbox`|`/inbox` (XML)
- GET `/provider/query/outbox/json`|`/inbox/json` → `DocumentQueryResponse`
- GET `/provider/query/outbox/pdf`|`/inbox/pdf` → `application/pdf`
  - Params: `startDate`, `endDate`, `documentType`, `queried`, `withXML`, `takenFromEntegrator`, `minRecordId`

### Query (e-Arşiv)
- GET `/provider/earchive/query/outbox` (XML)
- GET `/provider/earchive/query/outbox/json` → `DocumentQueryResponse`
- GET `/provider/earchive/query/outbox/pdf` → `application/pdf`
  - Params: `startDate`, `endDate`, `withXML`, `minRecordId`

### Webhooks
- GET `/webhooks`
- POST `/webhooks` { eventType, url, secret }
- DELETE `/webhooks/{id}`
- GET `/webhooks/{id}/deliveries`
- POST `/webhooks/deliveries/{deliveryId}/retry`

### API Keys
- GET `/apikeys`
- POST `/apikeys` { name, scopes }
- DELETE `/apikeys/{id}`


