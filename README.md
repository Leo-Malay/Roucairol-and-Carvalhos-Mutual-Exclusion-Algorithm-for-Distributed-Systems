# CS 6378: Advanced Operating Systems  
**Project 2**  
**Instructor:** Prof. Neeraj Mittal  
**Author:** Malay Virendra Bhavsar (MXB230055)

---

## Project Overview

This individual project aims to implement a distributed mutual exclusion service using Roucairol and Carvalho’s algorithm, providing cs-enter() and cs-leave() functions for process synchronization. Developed in **Java (Version 22.0.2)**, the project emphasizes socket programming and distributed communication, running exclusively on the `dcXX.utdallas.edu` machines.

---

## Project Components

The project comprises several essential components:

- **Node**: Sets up Client, Server instances; manages the configuration file.
  
- **Server**: Oversees incoming communications and directs them appropriately.
  
- **Client**: Manages outgoing communications, including sending key, requesting key. Also performs Critical Section Cycle (Blocking)
  
- **Message**: Establishes a structured format for inter-process message exchange.

---

## Project Segments

The project is divided into four main segments:

1. **Part 1**: Implement a distributed system with `n` nodes, each able to send and recieve messages.

2. **Part 2**: Implement Roucairol and Carvalho’s algorithm to syncronize the process

3. **Part 3**: Generate output in the format `{nodeId} {Scalar Clock Value} {CS_completed}`
   
4. **Part 4**: Testing for Critical Section Overlap.

---

## Configuration Format

The project utilizes a plain-text configuration file formatted as follows:

- The first line contains six tokens: 
    - `Number of nodes`
    - `Request Delay`
    - `Critical Section Execution Time`
    - `Number of Critical Sections per Request`
- The next `n` lines specify node details:
    - `nodeId`
    - `hostname`
    - `port`

---

## Output Format

For a configuration file named `<config_name>.txt` with `n` nodes, the program will generate a output files named `aos2.txt`. Each file contains nodeId, scalar clock timestamps for each critical section and the number of critical sections completed by process.

**Example Output:**
```
0 0 0
1 1 0
0 3 1
2 4 0
```
---

## Getting Started

### Prerequisites

- Ensure you have **Java Development Kit (JDK) 22.0.2** installed.
- This project must be executed on the machines `dcXX.utdallas.edu` (where XX ∈ {01, 02, ..., 45}).

### Setup Instructions

1. **Create a project directory:**
   ```bash
   mkdir aos-project2
   cd aos-project2
   ```
   
2. Place all project files (including the configuration file) in this directory.

3. Compile the project:
  ```bash
  javac *.java
  ```

4. Clean up before and after running the program:
  ```bash
  chmod +x cleanup.sh
  ./cleanup.sh
  ```

5. Run the program:
  ```bash
  chmod +x launcher.sh
  ./launcher.sh
  ```

6. Perform cleanup after use:
  ```bash
  ./cleanup.sh
  ```

### Connecting to the Server

To connect to the server at `dcXX.utdallas.edu`, use the following command (omit the password):
```bash
ssh <your-username>@dcXX.utdallas.edu
```
Ensure that you replace <your-username> with your actual username. After connecting, you can navigate to your project directory to compile and run your code.

---

## Acknowledgments

The `./launcher.sh` and `./cleanup.sh` scripts are provided by the professor. All credits go to the respective owners for these contributions.

---

## Conclusion

This project delves into the intricacies of distributed systems, showcasing effective communication and state consistency through the Chandy-Lamport protocol. We encourage you to explore these concepts deeply and enhance your understanding.

If you have any questions or require further assistance, please don't hesitate to reach out!

---


