import React, { useState, useEffect, useRef } from 'react';
import { FaComments, FaTimes, FaPaperPlane } from 'react-icons/fa';
import './Chatbot.css';

export default function Chatbot() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    { 
      text: "Hello! I'm MediWay Assistant. How can I help you today?", 
      sender: 'bot',
      timestamp: new Date()
    }
  ]);
  const [input, setInput] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Common questions and answers for MediWay
  const knowledgeBase = {
    greetings: {
      keywords: ['hello', 'hi', 'hey', 'good morning', 'good afternoon', 'good evening'],
      response: "Hello! Welcome to MediWay. How can I assist you with your healthcare needs today?"
    },
    appointment: {
      keywords: ['appointment', 'book', 'schedule', 'booking', 'make appointment'],
      response: "To book an appointment:\n1. Go to the Home page\n2. Click on 'Book Appointment' button\n3. Fill in your details and select a doctor\n4. Choose your preferred date and time\n5. Submit the form\n\nYou can view your appointments in the Appointments section."
    },
    registration: {
      keywords: ['register', 'sign up', 'create account', 'new account', 'registration'],
      response: "To register on MediWay:\n1. Click on 'Register' in the navigation menu\n2. Fill in your personal details (name, email, password)\n3. Provide your health information\n4. Submit the form\n\nYou'll receive a unique Health ID upon successful registration."
    },
    login: {
      keywords: ['login', 'sign in', 'log in', 'access account'],
      response: "To login:\n1. Click on 'Login' in the navigation menu\n2. Enter your Health ID and password\n3. Click 'Login'\n\nFor doctors, use the 'Doctor Login' option. Admins have a separate admin login."
    },
    profile: {
      keywords: ['profile', 'my account', 'personal information', 'update profile'],
      response: "To view or update your profile:\n1. Login to your account\n2. Click on 'Profile' in the navigation menu\n3. View your medical records and personal information\n4. Update details as needed"
    },
    doctors: {
      keywords: ['doctor', 'doctors', 'find doctor', 'specialist', 'physician'],
      response: "You can find available doctors on our platform:\n1. Visit the Home page\n2. Browse through the list of available doctors\n3. View their specializations and availability\n4. Select a doctor when booking an appointment"
    },
    medicalRecords: {
      keywords: ['medical record', 'health record', 'medical history', 'reports', 'test results'],
      response: "To access your medical records:\n1. Login to your account\n2. Go to your Profile section\n3. View your complete medical history\n4. Download reports if needed\n\nYour medical records are securely stored and accessible only to you and your healthcare providers."
    },
    cancel: {
      keywords: ['cancel', 'cancel appointment', 'delete appointment', 'remove appointment'],
      response: "To cancel an appointment:\n1. Go to the 'Appointments' page\n2. Find the appointment you want to cancel\n3. Click the cancel or delete button\n4. Confirm the cancellation\n\nPlease cancel at least 24 hours in advance if possible."
    },
    payment: {
      keywords: ['payment', 'pay', 'cost', 'fee', 'price', 'charge'],
      response: "Payment information:\n- Consultation fees vary by doctor and specialization\n- Payment details will be shown during appointment booking\n- We accept various payment methods\n- You'll receive a receipt after payment confirmation"
    },
    hours: {
      keywords: ['hours', 'timing', 'open', 'available', 'time', 'when open'],
      response: "MediWay operates:\n- Platform: 24/7 online access\n- Appointment bookings: Anytime\n- Doctor availability varies by individual schedules\n- Check specific doctor timings when booking"
    },
    emergency: {
      keywords: ['emergency', 'urgent', 'immediate', 'critical', 'serious'],
      response: "⚠️ For medical emergencies:\n- Call emergency services (911/112) immediately\n- Visit the nearest emergency room\n- MediWay is for scheduled consultations, not emergencies\n\nFor urgent but non-emergency care, book the earliest available appointment."
    },
    contact: {
      keywords: ['contact', 'support', 'help', 'customer service', 'reach'],
      response: "Contact MediWay:\n- Email: support@mediway.com\n- Phone: +1-800-MEDIWAY\n- Chat: Use this chatbot for instant help\n- Response time: Within 24 hours"
    },
    forgot: {
      keywords: ['forgot password', 'reset password', 'forgot health id', 'lost password'],
      response: "To recover your account:\n1. Click 'Forgot Password' on the login page\n2. Enter your registered email\n3. Check your email for reset instructions\n4. Follow the link to create a new password\n\nFor Health ID recovery, contact support at support@mediway.com"
    },
    security: {
      keywords: ['security', 'privacy', 'safe', 'data protection', 'secure'],
      response: "Your security is our priority:\n- All data is encrypted and secure\n- HIPAA compliant platform\n- Your medical records are private\n- We never share your information without consent\n- Regular security audits performed"
    },
    prescription: {
      keywords: ['prescription', 'medicine', 'medication', 'drugs'],
      response: "Regarding prescriptions:\n- Doctors can prescribe medications during consultations\n- Prescriptions are stored in your medical records\n- You can view and download prescriptions from your profile\n- Take prescriptions to any pharmacy"
    },
    doctorSignup: {
      keywords: ['doctor signup', 'doctor registration', 'join as doctor', 'doctor account'],
      response: "For doctors to join MediWay:\n1. Click on 'Doctor Signup' in the navigation\n2. Provide your professional credentials\n3. Submit required documents\n4. Wait for verification (24-48 hours)\n5. Once approved, you can start accepting appointments"
    }
  };

  const quickActions = [
    "How to book an appointment?",
    "How to register?",
    "View my medical records",
    "Find a doctor",
    "Cancel appointment",
    "Contact support"
  ];

  const findBestMatch = (userInput) => {
    const input = userInput.toLowerCase().trim();
    
    // Check for greetings
    for (const keyword of knowledgeBase.greetings.keywords) {
      if (input.includes(keyword)) {
        return knowledgeBase.greetings.response;
      }
    }

    // Check each category
    for (const category in knowledgeBase) {
      if (category === 'greetings') continue;
      const { keywords, response } = knowledgeBase[category];
      for (const keyword of keywords) {
        if (input.includes(keyword)) {
          return response;
        }
      }
    }

    // Default response if no match found
    return "I'm not sure I understand. Here are some things I can help you with:\n\n" +
           "• Booking appointments\n" +
           "• Registration and login\n" +
           "• Viewing medical records\n" +
           "• Finding doctors\n" +
           "• Canceling appointments\n" +
           "• Payment information\n" +
           "• Contact support\n\n" +
           "Try asking a specific question or use the quick action buttons below!";
  };

  const handleSend = () => {
    if (input.trim() === '') return;

    // Add user message
    const userMessage = {
      text: input,
      sender: 'user',
      timestamp: new Date()
    };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setIsTyping(true);

    // Simulate bot thinking and respond
    setTimeout(() => {
      const botResponse = findBestMatch(input);
      const botMessage = {
        text: botResponse,
        sender: 'bot',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, botMessage]);
      setIsTyping(false);
    }, 1000);
  };

  const handleQuickAction = (action) => {
    setInput(action);
    handleSendWithText(action);
  };

  const handleSendWithText = (text) => {
    // Add user message
    const userMessage = {
      text: text,
      sender: 'user',
      timestamp: new Date()
    };
    setMessages(prev => [...prev, userMessage]);
    setIsTyping(true);

    // Simulate bot thinking and respond
    setTimeout(() => {
      const botResponse = findBestMatch(text);
      const botMessage = {
        text: botResponse,
        sender: 'bot',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, botMessage]);
      setIsTyping(false);
    }, 1000);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <>
      {/* Floating Chat Button */}
      {!isOpen && (
        <button 
          className="chatbot-button" 
          onClick={() => setIsOpen(true)}
          aria-label="Open chatbot"
        >
          <FaComments />
        </button>
      )}

      {/* Chat Window */}
      {isOpen && (
        <div className="chatbot-container">
          <div className="chatbot-header">
            <div className="chatbot-header-content">
              <FaComments className="chatbot-header-icon" />
              <div>
                <h3>MediWay Assistant</h3>
                <span className="chatbot-status">Online</span>
              </div>
            </div>
            <button 
              className="chatbot-close" 
              onClick={() => setIsOpen(false)}
              aria-label="Close chatbot"
            >
              <FaTimes />
            </button>
          </div>

          <div className="chatbot-messages">
            {messages.map((message, index) => (
              <div 
                key={index} 
                className={`chatbot-message ${message.sender === 'user' ? 'user-message' : 'bot-message'}`}
              >
                <div className="message-content">
                  {message.text.split('\n').map((line, i) => (
                    <React.Fragment key={i}>
                      {line}
                      {i < message.text.split('\n').length - 1 && <br />}
                    </React.Fragment>
                  ))}
                </div>
                <div className="message-time">
                  {message.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </div>
              </div>
            ))}
            
            {isTyping && (
              <div className="chatbot-message bot-message">
                <div className="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Quick Actions */}
          {messages.length <= 2 && (
            <div className="quick-actions">
              <div className="quick-actions-title">Quick questions:</div>
              <div className="quick-actions-buttons">
                {quickActions.map((action, index) => (
                  <button 
                    key={index}
                    className="quick-action-btn"
                    onClick={() => handleQuickAction(action)}
                  >
                    {action}
                  </button>
                ))}
              </div>
            </div>
          )}

          <div className="chatbot-input">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder="Type your question..."
              className="chatbot-input-field"
            />
            <button 
              onClick={handleSend}
              className="chatbot-send-btn"
              disabled={input.trim() === ''}
              aria-label="Send message"
            >
              <FaPaperPlane />
            </button>
          </div>
        </div>
      )}
    </>
  );
}
