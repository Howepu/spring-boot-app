// Main App component
const App = () => {
  const [page, setPage] = React.useState('home');
  const [selectedUserId, setSelectedUserId] = React.useState(null);
  
  // Handle navigation
  const navigate = (targetPage, userId = null) => {
    setPage(targetPage);
    if (userId !== null) {
      setSelectedUserId(userId);
    }
  };
  
  // Render the appropriate page based on current state
  const renderPage = () => {
    switch (page) {
      case 'users':
        return <UserList onUserSelect={(id) => navigate('userView', id)} onNewUser={() => navigate('userForm')} />;
      case 'userForm':
        return <UserForm onComplete={() => navigate('users')} userId={selectedUserId} />;
      case 'userView':
        return <UserView userId={selectedUserId} onBack={() => navigate('users')} onEdit={() => navigate('userForm', selectedUserId)} />;
      default:
        return <HomePage />;
    }
  };
  
  return (
    <div className="min-h-screen flex flex-col">
      <Header onNavigate={navigate} currentPage={page} />
      <main className="container mx-auto px-4 py-8 flex-grow">
        {renderPage()}
      </main>
      <footer className="bg-gray-800 text-white py-4">
        <div className="container mx-auto px-4 text-center">
          &copy; 2025 AI-Insight Dashboard
        </div>
      </footer>
    </div>
  );
};
