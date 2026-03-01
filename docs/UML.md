### 🏗️ Diagrama de Clases (UML)

| **Clase: User** | **Relación** | **Clase: JobOffer** |
| :--- | :---: | :--- |
| `Long id` (PK) | | `Long id` (PK) |
| `String username` | | `Long company_id` (FK) |
| `String email` | | `String title` |
| `String password` | **1 : N** | `String description` |
| `Role role` | ◄───── | `Category category` |
| `boolean active` | | `WorkMode workMode` |
| `String companyName` | | `Status status` |
| `String bio` | | `LocalDateTime createdAt` |

<br>

| **Clase: Application** | **Relaciones con el Sistema** |
| :--- | :--- |
| `Long id` (PK) | ◄── **N : 1** con **User** (`candidate_id`) |
| `Long candidate_id` (FK) | ◄── **N : 1** con **JobOffer** (`offer_id`) |
| `Long offer_id` (FK) | |
| `ApplicationStatus status` | |
| `LocalDateTime appliedAt` | |

### 📋 Enumeraciones (Enums)

| Role | Category | WorkMode | Status | ApplicationStatus |
| :--- | :--- | :--- | :--- | :--- |
| `USER` | `IT` | `REMOTE` | `ACTIVE` | `PENDING` |
| `COMPANY` | `DESIGN` | `HYBRID` | `CLOSED` | `REVIEWING` |
| `ADMIN` | `SALES` | `OFFICE` | | `REJECTED` |
| | `OTHERS` | | | `HIRED` |