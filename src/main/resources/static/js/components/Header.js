// Header component for navigation
const Header = ({ onNavigate, currentPage }) => {
  return (
    <header className="bg-gray-800 text-white">
      <nav className="container mx-auto px-4 py-4 flex flex-wrap items-center justify-between">
        <div className="flex items-center">
          <h1 className="text-xl font-bold">
            <a 
              href="#" 
              onClick={(e) => { 
                e.preventDefault(); 
                onNavigate('home'); 
              }}
              className="hover:text-gray-300 transition duration-200"
            >
              AI-Insight Dashboard
            </a>
          </h1>
        </div>
        
        <div>
          <ul className="flex space-x-6">
            <li>
              <a 
                href="#" 
                onClick={(e) => { 
                  e.preventDefault(); 
                  onNavigate('home'); 
                }}
                className={`hover:text-gray-300 transition duration-200 ${currentPage === 'home' ? 'text-white border-b-2 border-white' : 'text-gray-300'}`}
              >
                Главная
              </a>
            </li>
            <li>
              <a 
                href="#" 
                onClick={(e) => { 
                  e.preventDefault(); 
                  onNavigate('users'); 
                }}
                className={`hover:text-gray-300 transition duration-200 ${currentPage === 'users' || currentPage === 'userForm' || currentPage === 'userView' ? 'text-white border-b-2 border-white' : 'text-gray-300'}`}
              >
                Пользователи
              </a>
            </li>
          </ul>
        </div>
      </nav>
    </header>
  );
};
