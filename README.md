# Darkness Client

Бесплатный PvP-визуал и утилиты для **Minecraft 1.21.11 (Fabric)** — всё в одном моде.
Открытый код, без подписок и активаций. Альтернатива платным «визуал-клиентам».

## Установка (один .jar)

1. В лаунчере нужен **Fabric Loader 0.16.9+** для Minecraft **1.21.11**.
2. Скопируйте `darkness-client-1.0.1-mc1.21.11.jar` в папку `mods` — больше ничего ставить не нужно (Fabric API не требуется).
3. Запустите игру.

## Управление

| Действие | Клавиша / команда |
|---|---|
| Меню модулей | **Right Shift** |
| Зум | **C** (зажать, настраивается) |
| Элитры ⇄ нагрудник | **V** |
| Тотем в левую руку | **G** |
| Команды | `.help` в чате |

Команды: `.t <модуль>`, `.bind <модуль> <клавиша|none>`, `.config save|load|reset`,
`.accounts list|add|login|remove <ник>`, `.gui`, `.hud`, `.acc`.

## Модули (37)

**HUD (15):** Watermark, ArrayList (с фильтрами категорий), Keystrokes, FPS, Ping, CPS,
Coordinates, ArmorHUD, PotionHUD, TargetHUD, ComboCounter, ReachDisplay, RAM, ServerInfo,
**InventoryHUD** (инвентарь как элемент HUD). У каждого элемента настройка **«Масштаб»**
(0.5–2.0) — размер меняется в меню или HUD-редакторе.

**Visuals (10):** CustomCrosshair, DamageNumbers, HitParticles, Trajectories, Tracers, ESP,
Zoom, FullBright, **CustomHand** (смещение X/Y и масштаб руки), **WorldCustomizer**
(свой цвет неба и тумана, скрытие солнца/луны/звёзд).

**Оптимизация (9):** NoRain, NoHurtCam, NoEntityShadows, ParticleLimit, FpsBoost,
**NoFireOverlay** (огонь на экране), **NoWaterOverlay** (плёнка под водой),
**NoBlockOverlay** (тыква/порошок), **NoItemActivation** (анимация тотема на весь экран).
Все визуальные функции Optimизации и рендер-оверлеи работают по флагам и не пересчитывают
ничего лишнего в кадре (кэш сортировки ArrayList, редукция шагов траектории).

**Утилиты (3):** AutoReconnect, **ElytraSwap** (V — элитры ⇄ нагрудник),
**TotemSwap** (G — тотем в левую руку). Свапы работают из любого места: в инвентаре,
в руке, в мире — клики отправляются серверу напрямую.

## Minecraft Customize (своя картинка на главном экране)

1. Откройте меню (Right Shift) → кнопка **«Minecraft Customize (папка)»** — откроется
   `.minecraft/config/darkness/customize/`.
2. Положите туда **title.png** (лучше 1920×1080).
3. Перезайдите на главный экран игры — картинка будет фоном под меню.
   Изменение подхватывается автоматически при смене файла (можно менять на лету).

## Конфиги

`.minecraft/config/darkness/`:
- `config.json` — модули, настройки, бинды, позиции HUD
- `accounts.json` — аккаунты
- `customize/` — папка кастомизации (title.png)

## Сборка из исходников

Требуется **JDK 21** (проверено на Temurin/Oracle 21). Gradle скачивается сам через wrapper.

```bash
./gradlew build          # результат: build/libs/darkness-client-1.0.1-mc1.21.11.jar
./gradlew runClient      # запуск тестового клиента с модом (мир «Новый мир» через quickPlay)
```

## Как самому добавить модуль (5 минут)

1. Создайте класс в `module/modules/<категория>/`:

```java
public class MyModule extends Module {
    private final NumberSetting power;

    public MyModule() {
        super("MyModule", "Описание", Category.UTILITY);
        power = new NumberSetting("Сила", "Насколько сильно", 1, 0, 10, 0.5);
        register(power);
        setKeybind(GLFW.GLFW_KEY_H); // опционально
    }

    @Override
    public void onTick() {
        // вызывается каждый тик, пока модуль включён
    }
}
```

2. Зарегистрируйте в `DarknessClient.registerModules()`:
   `moduleManager.register(new MyModule());`

Типы настроек: `BooleanSetting`, `NumberSetting(имя, описание, дефолт, мин, макс, шаг)`,
`ModeSetting(имя, описание, List.of("A","B"), "A")`, `ColorSetting(имя, описание, 0xFFRRGGBB)`.
Всё само появится в меню, сохранится в конфиг и будет фильтроваться в ArrayList.
Для HUD-элемента наследуйтесь от `HudModule` и реализуйте `render(GuiGraphics, ...)`
— позиция, масштаб и перетаскивание появятся автоматически.

## Как добавить миксин

1. Класс в `dev.darkness.client.mixin` с `@Mixin(Цель.class)` и `@Inject`.
2. Добавьте его имя в `src/main/resources/darknessclient.mixins.json`.
3. Цели и сигнатуры сверяйте через javap (см. AGENTS.md) или genSources.

## Архитектура

- `DarknessClient` — точка входа, регистрация, тик, рендер-хуки
- `module/` — ядро (Module, HudModule, настройки) и `modules/` по категориям
- `mixin/` — 15 миксинов: HUD-хук, клавиатура, мышь-клики атак, чат-команды, погода,
  частицы, экранные эффекты, рука, небо, туман, тайтл-скрин, сессия, гамма
- `ui/` — ClickGUI (immediate-mode), HUD-редактор, аккаунты
- `accounts/` — менеджер аккаунтов (смена User без перезапуска)
- `util/` — RenderUtil (мировая проекция → 2D), CustomizeManager, конфиг, CPS, таргет

## Лицензия

MIT. Minecraft — торговая марка Mojang/Microsoft; проект с ними не связан.
Визуальные модули могут запрещаться правилами отдельных серверов — используйте на свой риск.
