// UserForm component for creating/editing users
const UserForm = ({ onComplete, userId }) => {
  const [user, setUser] = React.useState({ username: '', email: '' });
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState(null);
  const [isEdit, setIsEdit] = React.useState(false);

  // When the component loads, if userId is provided, fetch the user data
  React.useEffect(() => {
    if (userId) {
      fetchUser(userId);
      setIsEdit(true);
    } else {
      setIsEdit(false);
      setUser({ username: '', email: '' });
    }
  }, [userId]);

  // Fetch user data for editing
  const fetchUser = async (id) => {
    try {
      setLoading(true);
      const response = await fetch(`/api/users/${id}`);
      
      if (!response.ok) {
        throw new Error(`HTTP error: ${response.status}`);
      }
      
      const data = await response.json();
      setUser(data);
      setError(null);
    } catch (err) {
      setError(`Ошибка при загрузке данных пользователя: ${err.message}`);
      console.error('Error fetching user:', err);
    } finally {
      setLoading(false);
    }
  };

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      
      const url = isEdit ? `/api/users/${userId}` : '/api/users';
      const method = isEdit ? 'PUT' : 'POST';
      
      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error: ${response.status}`);
      }
      
      onComplete(); // Navigate back to list view
    } catch (err) {
      setError(`Ошибка при ${isEdit ? 'обновлении' : 'создании'} пользователя: ${err.message}`);
      console.error('Error submitting user form:', err);
    } finally {
      setLoading(false);
    }
  };

  // Handle input changes
  const handleChange = (e) => {
    const { name, value } = e.target;
    setUser(prev => ({ ...prev, [name]: value }));
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6 max-w-xl mx-auto">
      <h2 className="text-2xl font-bold text-gray-800 mb-6">
        {isEdit ? 'Редактирование пользователя' : 'Создание пользователя'}
      </h2>
      
      {loading && (
        <div className="flex justify-center items-center py-4">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
      )}
      
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}
      
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-1">
            Имя пользователя
          </label>
          <input
            type="text"
            id="username"
            name="username"
            value={user.username}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        
        <div>
          <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
            Email
          </label>
          <input
            type="email"
            id="email"
            name="email"
            value={user.email}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        
        <div className="flex justify-between pt-4">
          <button 
            type="button" 
            onClick={onComplete}
            className="px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300 transition duration-200"
          >
            Отмена
          </button>
          <button 
            type="submit"
            disabled={loading}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition duration-200 disabled:opacity-50"
          >
            {isEdit ? 'Обновить' : 'Создать'}
          </button>
        </div>
      </form>
    </div>
  );
};
