# Lost & Found Management System - System Architecture

## Overview

This document describes the complete architecture of the Lost & Found Management System.

---

## Mermaid Architecture Diagram

```mermaid
flowchart TD

A[User Browser]

A --> B[Login / Register JSP]
A --> C[Item Board JSP]
A --> D[Post Item JSP]
A --> E[Claim Form JSP]
A --> F[Admin Dashboard JSP]

subgraph Web Layer
B --> G[AuthServlet]
C --> H[ItemServlet]
D --> H
E --> I[ClaimServlet]
F --> J[AdminServlet]
K[Image Requests] --> L[ImageServlet]
end

subgraph Security Layer
M[AuthFilter]
end

G --> M
H --> M
I --> M
J --> M
L --> M

subgraph Service Layer
N[UserService]
O[ItemService]
P[ClaimService]
end

G --> N
H --> O
I --> P
J --> O
J --> P

subgraph DAO Layer
Q[UserDAO]
R[ItemDAO]
S[ClaimDAO]
end

N --> Q
O --> R
P --> R
P --> S

subgraph ORM Layer
T[Hibernate ORM]
end

Q --> T
R --> T
S --> T

subgraph Database
U[(MySQL Database)]
end

T --> U

subgraph File Storage
V[Uploads Folder]
end

L --> V
H --> V

subgraph Background Jobs
W[ExpiryJob]
end

W --> R
W --> U