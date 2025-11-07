This is a Spring Boot-based web application that serves as an integration layer with the Coze AI API platform. The project provides a complete solution for managing AI bots and conducting chat conversations through both backend APIs and a frontend user interface.

Key Components:

Backend (Java/Spring Boot)
API Integration: Uses the Coze OpenAPI SDK to interact with the Coze platform
REST Controllers: Exposes endpoints for workspace management, bot creation, and chat functionality
Authentication: Implements token-based authentication with the Coze API
Security: Configured with Spring Security for endpoint protection and CORS handling

Frontend
Chat UI: A responsive chat interface styled similar to Facebook Messenger
Bot Management: Allows users to select and interact with different AI bots
Real-time Messaging: Supports sending and receiving messages through the Coze API
Core Features

Workspace Management: Lists available workspaces from Coze platform

Bot Operations: Create, list, and publish bots within workspaces

Chat Functionality: Create chat sessions and exchange messages with bots

File Handling: Upload bot avatars and other media

Technical Architecture:
Backend: Spring Boot with Spring Security and Spring Web MVC
Frontend: HTML/CSS/JavaScript with custom styling
API Communication: Both direct SDK usage and REST API endpoints
Authentication: Token-based authentication with login functionality

This project serves as a foundation for building AI-powered chat applications using the Coze platform, providing both backend services and a user-friendly chat interface.
