# BackOffice Framework

A modern, scalable backend administration system built with Node.js and Express.

## Features

- 🔐 **Secure Authentication**: Built-in authentication system with session management
- 📊 **Dashboard**: Clean and intuitive dashboard interface
- 👥 **User Management**: Comprehensive user administration tools
- ⚙️ **Settings**: Flexible configuration options
- 🎨 **Responsive Design**: Mobile-friendly interface

## Prerequisites

- Node.js (v14 or higher)
- npm (v6 or higher)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/Fenitral/projet-framework-backOffice.git
cd projet-framework-backOffice
```

2. Install dependencies:
```bash
npm install
```

3. Create a `.env` file from the example:
```bash
cp .env.example .env
```

4. Configure your environment variables in `.env` (optional)

## Usage

### Development

Start the development server:
```bash
npm run dev
```

### Production

Start the production server:
```bash
npm start
```

The application will be available at `http://localhost:3000`

## Default Credentials

For testing purposes, use these credentials:
- **Username**: `admin`
- **Password**: `admin123`

## Project Structure

```
projet-framework-backOffice/
├── src/
│   ├── config/         # Configuration files
│   ├── middleware/     # Custom middleware
│   ├── routes/         # Route definitions
│   ├── app.js          # Express app setup
│   └── server.js       # Server entry point
├── views/              # EJS templates
│   ├── auth/           # Authentication views
│   ├── dashboard/      # Dashboard views
│   └── partials/       # Reusable view components
├── public/             # Static assets
│   └── css/            # Stylesheets
├── .env.example        # Environment variables template
├── .gitignore          # Git ignore rules
├── package.json        # Project dependencies
└── README.md           # This file
```

## Available Routes

### Public Routes
- `GET /` - Home page
- `GET /auth/login` - Login page
- `POST /auth/login` - Login form submission
- `GET /auth/logout` - Logout

### Protected Routes (require authentication)
- `GET /dashboard` - Main dashboard
- `GET /dashboard/users` - User management
- `GET /dashboard/settings` - System settings

## Technologies Used

- **Node.js** - Runtime environment
- **Express** - Web framework
- **EJS** - Templating engine
- **Express Session** - Session management
- **bcryptjs** - Password hashing
- **body-parser** - Request body parsing
- **cookie-parser** - Cookie parsing
- **dotenv** - Environment variable management

## Security Features

- Session-based authentication
- Password hashing with bcrypt
- Protected routes with authentication middleware
- Secure cookie handling (httpOnly, sameSite)
- Environment-based cookie security (secure flag in production)

## Security Considerations

This is a demo/starter framework. For production use, consider adding:
- **CSRF Protection**: Implement CSRF tokens for form submissions (e.g., using a modern CSRF library)
- **Rate Limiting**: Add rate limiting to prevent brute force attacks
- **Database**: Replace mock user data with a proper database
- **Input Validation**: Add comprehensive input validation and sanitization
- **HTTPS**: Always use HTTPS in production
- **Security Headers**: Add security headers using helmet.js

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

ISC