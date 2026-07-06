# Uber Clone — Ride-Sharing Backend

A minimal ride-sharing backend built as a Spring Boot microservices system, that models the core ride lifecycle (booking → driver matching → acceptance → completion → review).

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Services](#services)
- [Core Ride Lifecycle](#core-ride-lifecycle)
- [Tech Stack](#tech-stack)
- [Running Locally](#running-locally)

---

## Architecture Overview

```
                              Client
                                │
                                ▼
                      ┌───────────────────┐
                      │   UberApiGateway   │
                      │ (Spring Cloud      │
                      │   Gateway)         │
                      └─────────┬──────────┘
                                │ routes requests
          ┌─────────────┬───────┼────────────┬──────────────┐
          ▼             ▼       ▼            ▼              ▼
   ┌─────────────┐ ┌──────────────┐ ┌──────────────────┐ ┌────────────────┐
   │ UberAuth     │ │ UberBooking  │ │ UberLocation      │ │ UberReview     │
   │ Service      │ │ Service      │ │ Service           │ │ Service        │
   │ (JWT issuing)│ │              │ │ (Redis GEO)       │ │                │
   └──────────────┘ └──────┬───────┘ └─────────▲─────────┘ └────────────────┘
                            │                    │
                            │  Feign: nearby     │
                            │  drivers           │
                            └────────────────────┘
 
                            │
                            │  Feign: raise ride request
                            ▼
                   ┌───────────────────┐
                   │  UberSocketServer  │
                   │  (WebSocket/STOMP) │
                   └─────────┬──────────┘
                             │ pushes ride requests to
                             │ drivers, relays accept/reject
                             ▼
                     Driver clients
 
   All services above register with:
 
                   ┌───────────────────────┐
                   │   UberServiceDiscovery │
                   │       (Eureka)         │
                   └───────────────────────┘
 
   All services also depend on (compile-time, not a running service):
 
                   ┌───────────────────────┐
                   │    UberEntityService   │
                   │  shared JPA entities   │
                   │ (Driver, Booking, etc.)│
                   └───────────────────────┘
```

All services register with Eureka and communicate over **OpenFeign**.

---

## Services

| Service | Responsibility |
|---|---|
| **UberServiceDiscovery** | Eureka registry — service discovery for all other services |
| **UberApiGateway** | Single public entry point. Verifies JWTs from cookies, injects trusted `X-User-Id`/`X-User-Role` headers, routes to backend services via Eureka (`lb://SERVICE-NAME`) |
| **UberAuthService** | Passenger/driver signup, passenger sign-in, JWT issuance (userId + role claims), password hashing (BCrypt) |
| **UberBookingService** | Booking lifecycle: creation, driver-nearby dispatch, atomic ride acceptance, ride completion |
| **UberLocationService** | Driver geolocation via Redis GEO commands; validates driver identity against MySQL before accepting writes; TTL-based online/liveness tracking |
| **UberSocketServer** | WebSocket/STOMP layer — pushes ride-request pop-ups to subscribed drivers, relays accept/reject responses back to `UberBookingService` |
| **UberReviewService** | Post-ride passenger reviews; aggregates rating into the driver's running average |
| **UberEntityService** | Shared JPA entity library (not a runnable service) — `Driver`, `Passenger`, `Booking`, `Review`, etc. Published to `mavenLocal()` and consumed as a Gradle dependency by every service above |

---

## Core Ride Lifecycle

1. **Booking created** — passenger creates a booking; status set to `ASSIGNING_DRIVER`.
2. **Nearby driver fetch** — `UberLocationService` looks up nearby drivers using Redis, within a 5km radius.
3. **Ride request broadcast** — nearby drivers are notified in real time via `UberSocketServer`.
4. **Driver accepts** — the first driver to accept gets the booking; the booking status updates and other drivers are informed the ride is no longer available.
5. **Ride completes** — booking status is updated to `COMPLETED`.
6. **Review** — passenger reviews the completed ride, and the driver's rating is updated accordingly.

---

## Tech Stack

- **Java 21**, **Spring Boot 3.5.6**, Gradle
- **Spring Cloud** — Eureka (service discovery), Gateway, OpenFeign
- **Spring Security** — JWT-based auth, BCrypt password hashing
- **MySQL** — primary relational store (shared instance across services)
- **Redis** — driver geolocation (`GEOADD`/`GEORADIUS`) and TTL-based online-status tracking
- **WebSocket / STOMP (SockJS)** — real-time ride-request delivery to drivers
- **Docker** — Redis containerization

---

## Running Locally

> Prerequisite services: MySQL running on `localhost:3306` (`UberDB` schema), Redis (Dockerized), and each Spring Boot service run individually.

1. Start `UberServiceDiscovery` first (Eureka) — everything else registers with it.
2. Start Redis via Docker, mapped to a **non-default host port** (this project maps it to `6380` rather than `6379`, to avoid conflicting with any other local Redis installation):
   ```bash
   docker run -d --name redis -p 6380:6379 redis:alpine
   ```
3. Ensure `UberEntityService` is published to your local Maven repository before building any consuming service:
   ```bash
   ./gradlew publishToMavenLocal
   ```
4. Start the remaining services in any order: `UberAuthService`, `UberLocationService`, `UberBookingService`, `UberSocketServer`, `UberReviewService`, and finally `UberApiGateway`.
5. All client traffic goes through the gateway (default port `8080`); no service should be called directly in normal operation.
