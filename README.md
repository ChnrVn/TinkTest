# Торговый бот на Tinkoff Invest API
Торговый бот реализующий торговлю инструментами (на данном этапе фондами и акциями), основывающий решения о покупке/продаже на основе
<a href="https://en.wikipedia.org/wiki/Relative_strength_index">RSI</a>. Есть возможность добавлять/удалять инструменты, которыми ведется торговля,
ограничивать количество средств, доступных боту для торговли одним инструментом. Управление ботов ведется через ввод команд в консоль. Подробнее о доступных командах ниже.

## Структура проекта
Торговый бот реализован в качестве интерактивного консольного приложения, есть возможность быстрого расширения функционала в виде подключения к базе данных и графического пользовательского интерфейса.
Логически приложение разделено на четыре части:
1. Взаимодействие с пользователем
Получение команд, обработка запросов и исключительных ситуаций
2. Вычислительная
По полученным данным вычисляются коэффициенты (RSI, NVI, PVI; в данной программе реализован только RSI), по ним принимаются решения о купле и продаже
3. Соединительная
Создание потоков и унарных запросов к API для получения и отправки данных, на основе принятых решений и вычисленных индексов
4. Хранение данных
Хранение истории всех сделок, компании с доступными компаниями для торговли и индексами
## Установка и запуск
Установите java 11 и выше
Далее откройте командную строку (в Windows от имени администратора) и перейдите в директорию с jar файлом
Введите java -jar НазваниеФайла.jar
## Начало работы
При запуске бота будет выведен запрос на введение токена. Токен можно сгенерировать на сайте Тинькофф инвестиции в разделе токен.
Далее нужно выбрать номер аккаунта, по которому будет вестись торговля. Затем включается интерактивный режим с возможностью ввода команд.
Первой введите команду help для ознакомления со всеми возможностями бота.

## Доступные команды
<ul>
<li>help - список всех команд
 <li>change-parameters - Можно изменить количество денегб доступных для торговли инструментом, стоплосс, тейкпрофит
<li>add - Добавление инструмента. Нужно ввести FIGI инструмента, деньги, предоставляемые боту для торговли инструментом, максимальный процент просадки, после которого происходит продажа, процент прибыли, после которого бот начинает продавать инструмент
<li>startTrade - Начать торговать инструментом. Команда add лишь добавляет инструмент в программу, для торговли используйте эту команду
<li>stopTrade - Перестать торговать инструментом
<li>delete - Удалить инструмент из программы
<li>printSchedule - Вывести расписание бирж
<li>exit - Выход из программы. Вся торговля останавливается. На данном этапе при следующем запуске программа не будет помнить торги с предыдущей сессии


