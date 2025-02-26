# TestPoints

TestPoints – это Android-приложение, разработанное для запроса координат точек с сервера и их отображения в виде таблицы и графика.

---

## Техническое задание

**Цель проекта:**  
Создать мобильное приложение для Android на **Kotlin** (или Java), которое запрашивает с сервера определённое количество точек (координаты x и y) и отображает полученный ответ в виде таблицы и графика.

**Основной функционал:**

- **Главный экран (MainFragment):**
  - Блок информационного текста.
  - Поле для ввода числа точек.
  - Кнопка «Поехали», при нажатии на которую выполняется запрос к API.

- **Запрос к API:**
  - **Метод:** GET
  - **Эндпоинт:** `/api/test/points`
  - **Параметры:** `count` – количество точек.
  - **Пример ответа (JSON):**
    ```json
    {
      "points": [
        { "x": 1.23, "y": 2.44 },
        { "x": 2.17, "y": 3.66 }
      ]
    }
    ```
  - Сервер возвращает ошибку при неверном количестве точек или при сбоях.

- **Экран отображения результатов (PointsFragment):**
  - Таблица, показывающая полученные координаты точек.
  - График, на котором точки соединены прямыми линиями, а точки отсортированы по возрастанию координаты x.

**Дополнительные возможности (опционально):**

- **Масштабирование графика пользователем:**  
  ✔️ Реализовано библиотекой
- **Сглаживание линий графика (не ломаная, а сглаженная):**  
  ✔️ Реализовано библиотекой
- **Поддержка портретной и ландшафтной ориентаций экрана:**  
  ✔️ Реализовано через дополнительные вью и специальную логику для RecyclerView
- **Сохранение графика в файл:**  
  ❌ Нереализовано


---

## Видео-презентация

Посмотрите видео-презентацию проекта на YouTube:  
[https://www.youtube.com/watch?v=LCn4ffcLLvo](https://www.youtube.com/watch?v=LCn4ffcLLvo)

---

## Заключение

Приложение TestPoints реализует запрос к API для получения набора точек и их отображение в виде таблицы и графика. Приложение построено с использованием принципов Clean Architecture и паттерна MVVM, что обеспечивает модульность, тестируемость и масштабируемость проекта.