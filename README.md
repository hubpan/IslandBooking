<h3 align="center">Island Booking API service</h3>
<p align="center">
    API service to make reservation for campsite at Island Resort
</p>

<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#implementation-notes">Implementation Notes</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

An API service powered by Spring Boot; allow reservation to be made for an
imaginary campsite at Island Resort.

### Built With

* Spring Boot 2.5.3
* Spring Web
* Spring Data JPA
* H2 Database
* Liquibase Migration
* Lombok
* Bean Validation with Hibernate Validator
* Spring fox with Swagger V2 

<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

* Java 11

### Build

```sh
   # compile and run all tests
   ./gradlew clean build
```

### Run locally

```sh
   # server will be running at port 8080
   ./gradlew bootRun
```

<!-- USAGE EXAMPLES -->
## Usage

Interactive swagger documentation can be found at http://localhost:8080/swagger-ui/

Two main resource end-points are:
* /api/campsites
* /api/reservations

See the swagger documentation for more details about them.

<!-- Implementation Notes -->
## Implementation Notes

I am aware of the requirement that there is only one campsite on the island, but I decide to be
flexible in terms of managing possible more than one campsite.

To simplify calculation of times, I made assumption that jvm timezone can be pre-defined 
(I set it to "US/Hawaii"), so that both web application and H2 database are running with the 
same timezone. In addition, I adjusted time-range in the service layer to ensure that we are
always evaluating time-range with at least 24 hours; start-time is clipped to 00:01, while end-time
is clipped to 00:00.

#### Handling reservation conflicts

An elegant solution would be similar to the one described in postgresql documentation (>=9.2, https://www.postgresql.org/docs/current/rangetypes.html]),
where time-range can be declared as a single data-type, supported by GIST index (not limited by lack of scalar equality). 
Handling conflict is no different from common cases of enforcing unique-constraint on columns.

Since I am limited to using H2 database, I decide to go for optimistic locking against the associated entity "campsite".
This has an obvious downside of failing non-conflicting requests that are targeting the same campsite, but offers better
throughput than pessimistic locking. The false-positive failures can be somewhat reduced by trying out the same
failing reservation again when encountering ObjectOptimisticLockingFailureException.

#### Performance concerns for calculating campsite availability

If the client behaviour is well understood (e.g. most users are looking at a 2-week availability at landing page), 
then we could possibly improve performance by caching results of commonly queried parameters. Another optimisation
strategy is to partition the table in a way where we don't have to scan any bookings in the past. 

To help the general cases of applying arbitrary parameters, I have created a multi-column index:
```sql
CREATE INDEX idx_booking_camp_range ON booking(campsite_id, from_ts, to_ts DESC);
```




  










