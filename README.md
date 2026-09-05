# Darkness Client

Бесплатный PvP-визуал и утилиты для **Minecraft 1.21.11 (Fabric)** — всё в одном моде.
Альтернатива платным «визуал-клиентам» (типа Pulse Visuals): открытый код, без подписок и активаций.

## Установка (один .jar)

1. Убедитесь, что в лаунчере установлен **Fabric Loader 0.16.9+** для Minecraft **1.21.11**
   (на большинстве лаунчеров профиль создаётся один раз; сам мод ничего доустанавливать не требует).
2. Скопируйте `darkness-client-<версия>.jar` в папку `mods` вашего `.minecraft`.
3. Запустите игру. Никаких дополнительных зависимостей (в т.ч. Fabric API) не нужно.

Конфиги и аккаунты хранятся в `.minecraft/config/darkness/` (`config.json`, `accounts.json`).

## Управление

| Действие | Клавиша / команда |
|---|---|
| Открыть меню модулей | **Right Shift** |
| Зум (по умолчанию) | **C** (настраивается) |
| Команды в чате | `.help` |
| Редактор HUD | кнопка в меню или `.hud` |
| Менеджер аккаунтов | кнопка в меню или `.acc` |

## Команды

- `.help` — список команд
- `.t <модуль>` — включить/выключить модуль
- `.bind <модуль> <клавиша|none>` — привязать клавишу
- `.config save|load|reset` — конфигурация
- `.accounts list|add <ник>|login <ник>|remove <ник>` — аккаунты
- `.gui` / `.hud` / `.acc` — открыть экраны

## Модули (26)

**HUD:** Watermark, ArrayList, Keystrokes (WASD/мышь/CPS), FPS, Ping, CPS, Coordinates,
ArmorHUD (прочность), PotionHUD, TargetHUD (лицо, HP, броня, дистанция), ComboCounter,
ReachDisplay, RAM, ServerInfo.

**Visuals:** CustomCrosshair (стили, динамика), DamageNumbers (урон всплывающими числами),
HitParticles (искры при попадании), Trajectories (траектория стрел/жемчугов), Tracers,
ESP (2D-рамки), Zoom (плавный, настраиваемый), FullBright.

**Оптимизация:** NoRain, NoHurtCam, NoEntityShadows, ParticleLimit, FpsBoost
(минимум частиц, без облаков/виньетки одним кликом).

**Утилиты:** AutoReconnect (авто-вход после кика).

**Менеджер аккаунтов** — оффлайн-аккаунты: добавляйте ники, переключайтесь одним кликом
без перезапуска игры (на серверах нужно переподключиться). Microsoft-аккаунты — в планах.

## Сборка из исходников

Требуется JDK 21.

```bash
./gradlew build
# результат: build/libs/darkness-client-*.jar
```

## Структура проекта

- `dev.darkness.client.module` — ядро модулей и настроек
- `dev.darkness.client.module.modules.*` — модули по категориям
- `dev.darkness.client.ui` — ClickGUI, HUD-редактор, экран аккаунтов
- `dev.darkness.client.mixin` — миксины (рендер HUD, атака, чат, погода, частицы, сессия)
- `dev.darkness.client.accounts` — менеджер аккаунтов
- `dev.darkness.client.util` — проекция мира на экран, конфиг, CPS, таргет-трекер и пр.

## Лицензия

MIT. Minecraft — торговая марка Mojang/Microsoft; проект не связан с ними.
Использование на серверах с анти-читом — под вашу ответственность: визуальные модули
могут запрещаться правилами конкретных серверов.
