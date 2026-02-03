**Project Description:** A SpaceShooter type of game but instead of ships and space rock, it is a nyan_cat (the player) shoot at enemies (nyan_dog) and instead of rocks 
in space, we have floating cucumbers because cat is scared of cucumber.
**AI's agents**:
- ChatGPT
- Claude
- Deepseek
  
**AI's prompts**:
  (kimseak's)
- How to shoot bullets automatically
- Why bullets sometimes have gaps when moving left/right
- Why bullet speed decreases over time
- How to decide correct values (not magic numbers) for bullet removal
- How to handle bullets when using Circle instead of Rectangle
- What spawns bullets every frame (timers / onUpdate, not magic)
- Difference between EntityType and Entity
- What an Entity is vs what a Component is
- In BulletComponent, what entity refers to
- Why we must extend Component
- Why logic should live in components, not the main game class
- What EntityFactory is and what it does
- Why we implement EntityFactory
- Why use @Spawns("bullet")
- Why use spawn("bullet") instead of new Entity()
- Why BulletComponent and SpaceFactory are in separate .java files
- Why factories are better for scaling, cleanliness, and FXGL systems
  
  (Senghab's)
- Add features - Boss battle, leaderboard
- Optimize code - Enemy spawning, difficulty scaling
- Added pause/resume functionality - Fixed pause system that was causing game glitches
- Image background removal - Provided code to remove white backgrounds from sprites programmatically
- File organization advice - Explained project structure for resources (where to put images, which files to modify)
- Created modular file structure - Split code into separate manager classes (LeaderboardManager, FireModeManager, CollisionHandler) for easier debugging
- Added background images - Helped set up static and animated backgrounds (images, GIFs, starfield effects)
- Implemented GIF animation - Created GifPlayerComponent to play animated GIF files for player sprite
- Added automatic weapon upgrades - Fire mode changes based on score (single → double → triple shots)

  (Faris's)
1. Guideline for  a “YOU WON” message when the boss is defeated : • The text should appear in the center of the screen
 • It should not instantly close the game
2. How Enemies spawn near the top of the screen in the same row but different columns : Enemies should spawn near the top edge of the screen
 • They must spawn in the same row (same Y position)
 • Each enemy appears in a different column, evenly spaced across the screen
 • Enemy count scales with the player’s score
3. All enemies shoot at the same time using a synchronized system :  • Enemies shoot at the same time (synchronized firing)
 • Shooting is controlled by one global timer, not individual enemy timers
4.Enemy shooting speed increases as the player’s score gets higher : show when player score increase then shooting speed also increases 
5. How to put red flash damage : put the red flash overlay when player hits obstacle, got attacked by enemy and boss.

