// UserView component for viewing user details
const UserView = ({ userId, onBack, onEdit }) => {
  const [user, setUser] = React.useState(null);
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState(null);

  // Fetch user data when component mounts
  React.useEffect(() => {
    if (userId) {
      fetchUser(userId);
    }
  }, [userId]);

  // Fetch user details
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

  // Handle user deletion
  const handleDelete = async () => {
    if (!window.confirm('Вы уверены, что хотите удалить этого пользователя?')) {
      return;
    }
    
    try {
      setLoading(true);
      const response = await fetch(`/api/users/${userId}`, {
        method: 'DELETE'
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error: ${response.status}`);
      }
      
      onBack(); // Navigate back to list after successful deletion
    } catch (err) {
      setError(`Ошибка при удалении пользователя: ${err.message}`);
      console.error('Error deleting user:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6 max-w-xl mx-auto">
      <h2 className="text-2xl font-bold text-gray-800 mb-6">
        Просмотр пользователя
      </h2>
      
      {loading && (
        <div className="flex justify-center items-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
      )}
      
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}
      
      {!loading && !error && user && (
        <div className="space-y-4">
          <div className="bg-gray-50 p-4 rounded-lg">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-gray-500">ID</p>
                <p className="font-medium">{user.id}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Имя пользователя</p>
                <p className="font-medium">{user.username}</p>
              </div>
              <div className="md:col-span-2">
                <p className="text-sm text-gray-500">Email</p>
                <p className="font-medium">{user.email}</p>
              </div>
            </div>
          </div>
          
          <div className="flex justify-between pt-4">
            <button 
              onClick={onBack}
              className="px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300 transition duration-200"
            >
              Назад
            </button>
            
            <div className="space-x-2">
              <button 
                onClick={onEdit}
                className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition duration-200"
              >
                Редактировать
              </button>
              <button 
                onClick={handleDelete}
                className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition duration-200"
              >
                Удалить
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
