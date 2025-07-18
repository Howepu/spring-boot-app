/**
 * JavaScript для документации проекта Spring Boot Ollama Integration
 * Добавляет интерактивные функции для навигации по документации
 */
document.addEventListener('DOMContentLoaded', function() {
    // Добавляем активный класс к текущему пункту меню
    const navLinks = document.querySelectorAll('.nav-link');
    const currentPath = window.location.pathname;
    
    navLinks.forEach(link => {
        if (link.getAttribute('href') === '#' + window.location.hash.substring(1) || 
            link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });

    // Обработка клика по ссылкам навигации для плавного скролла
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href').substring(1);
            const targetElement = document.getElementById(targetId);
            
            if (targetElement) {
                window.scrollTo({
                    top: targetElement.offsetTop - 20,
                    behavior: 'smooth'
                });
                
                // Обновляем активный класс в меню
                navLinks.forEach(link => link.classList.remove('active'));
                this.classList.add('active');
                
                // Обновляем URL без перезагрузки страницы
                history.pushState(null, null, '#' + targetId);
            }
        });
    });

    // Подсветка примеров кода
    const codeBlocks = document.querySelectorAll('pre code');
    if (codeBlocks.length > 0) {
        highlightCode(codeBlocks);
    }

    // Добавляем обработчик для мобильного меню
    const sidebar = document.querySelector('.sidebar');
    if (sidebar && window.innerWidth < 768) {
        createMobileMenuToggle();
    }
});

/**
 * Добавляет простую подсветку синтаксиса к блокам кода
 * @param {NodeList} codeBlocks - коллекция элементов code
 */
function highlightCode(codeBlocks) {
    codeBlocks.forEach(block => {
        // Определяем тип кода (JSON, XML, HTML, и т.д.)
        const content = block.textContent;
        if (content.trim().startsWith('{') || content.trim().startsWith('[')) {
            // Это JSON
            try {
                const parsed = JSON.parse(content);
                block.textContent = JSON.stringify(parsed, null, 2);
                block.classList.add('json');
            } catch (e) {
                // Если парсинг не удался, оставляем как есть
            }
        }
    });
}

/**
 * Создает кнопку переключения меню для мобильных устройств
 */
function createMobileMenuToggle() {
    const container = document.querySelector('.container-fluid');
    const sidebar = document.querySelector('.sidebar');
    
    const toggleButton = document.createElement('button');
    toggleButton.className = 'btn btn-sm btn-primary menu-toggle';
    toggleButton.textContent = 'Меню';
    toggleButton.style.position = 'fixed';
    toggleButton.style.top = '10px';
    toggleButton.style.left = '10px';
    toggleButton.style.zIndex = '1050';
    
    toggleButton.addEventListener('click', function() {
        sidebar.classList.toggle('show');
    });
    
    container.prepend(toggleButton);
    
    // По умолчанию меню скрыто на мобильных устройствах
    sidebar.classList.remove('show');
}
