package net.gtaun.shoebill.test;

import net.gtaun.shoebill.constant.*;
import net.gtaun.shoebill.data.*;
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.exception.IllegalLengthException;
import net.gtaun.shoebill.entities.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by marvin on 07.04.16.
 * Copyright (c) Marvin Haschker 2016.
 */
public class NullPlayer extends Player {

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public int getId() {
        return 0;
    }

    @NotNull
    @Override
    public PlayerKeyState getKeyState() {
        return null;
    }

    @NotNull
    @Override
    public PlayerAttach getAttach() {
        return null;
    }

    @NotNull
    @Override
    public PlayerWeaponSkill getWeaponSkill() {
        return null;
    }

    @Override
    public int getPing() {
        return 12;
    }

    @Override
    public int getTeam() {
        return 0;
    }

    @Override
    public int getSkin() {
        return 1;
    }

    @Override
    public int getWantedLevel() {
        return 0;
    }

    @Override
    public int getCodepage() {
        return 1252;
    }

    @NotNull
    @Override
    public String getIp() {
        return "127.0.0.1";
    }

    @NotNull
    @Override
    public String getName() {
        return "TestPlayer";
    }

    @NotNull
    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public long getUpdateCount() {
        return 2;
    }

    @Override
    public float getHealth() {
        return 100;
    }

    @Override
    public float getArmour() {
        return 0;
    }

    @NotNull
    @Override
    public WeaponModel getArmedWeapon() {
        return WeaponModel.BRASSKNUCKLE;
    }

    @Override
    public void setArmedWeapon(WeaponModel weaponModel) {

    }

    @Override
    public int getArmedWeaponAmmo() {
        return 1;
    }

    @Override
    public int getMoney() {
        return 0;
    }

    @Override
    public int getScore() {
        return 0;
    }

    @Override
    public int getCameraMode() {
        return 0;
    }

    @Override
    public float getCameraAspectRatio() {
        return 0;
    }

    @Override
    public float getCameraZoom() {
        return 0;
    }

    @NotNull
    @Override
    public FightStyle getFightStyle() {
        return FightStyle.NORMAL;
    }

    @Override
    public Vehicle getVehicle() {
        return null;
    }

    @Override
    public int getVehicleSeat() {
        return 0;
    }

    @NotNull
    @Override
    public SpecialAction getSpecialAction() {
        return null;
    }

    @Override
    public Player getSpectatingPlayer() {
        return null;
    }

    @Override
    public Vehicle getSpectatingVehicle() {
        return null;
    }

    @Override
    public float getAngle() {
        return 0;
    }

    @NotNull
    @Override
    public AngledLocation getLocation() {
        return new AngledLocation(0f, 0f, 0f, 0f);
    }

    @NotNull
    @Override
    public Area getWorldBound() {
        return new Area(-3000, -3000, 3000, 3000);
    }

    @NotNull
    @Override
    public Velocity getVelocity() {
        return new Velocity(0f, 0f, 0f);
    }

    @NotNull
    @Override
    public PlayerState getState() {
        return PlayerState.ONFOOT;
    }

    @Override
    public Checkpoint getCheckpoint() {
        return null;
    }

    @Override
    public RaceCheckpoint getRaceCheckpoint() {
        return null;
    }

    @Override
    public DialogId getDialog() {
        return null;
    }

    @Override
    public boolean isStuntBonusEnabled() {
        return false;
    }

    @Override
    public boolean isSpectating() {
        return false;
    }

    @Override
    public boolean isRecording() {
        return false;
    }

    @Override
    public boolean isControllable() {
        return true;
    }

    @Override
    public void setCodepage(int i) {

    }

    @Override
    public void setName(String s) throws IllegalArgumentException, IllegalLengthException, AlreadyExistException {

    }

    @Override
    public void setColor(Color color) {

    }

    @Override
    public void setHealth(float v) {

    }

    @Override
    public void setArmour(float v) {

    }

    @Override
    public void setWeaponAmmo(WeaponModel weaponModel, int i) {

    }

    @Override
    public void setMoney(int i) {

    }

    @Override
    public void giveMoney(int i) {

    }

    @Override
    public void setScore(int i) {

    }

    @Override
    public void setFightStyle(FightStyle fightStyle) {

    }

    @Override
    public void setVehicle(Vehicle vehicle, int i) {

    }

    @Override
    public void setVehicle(Vehicle vehicle) {

    }

    @Override
    public void setLocation(float v, float v1, float v2) {

    }

    @Override
    public void setLocation(Vector3D vector3D) {

    }

    @Override
    public void setLocation(Location location) {

    }

    @Override
    public void setLocation(AngledLocation angledLocation) {

    }

    @Override
    public void setLocationFindZ(float v, float v1, float v2) {

    }

    @Override
    public void setLocationFindZ(Vector3D vector3D) {

    }

    @Override
    public void setLocationFindZ(Location location) {

    }

    @Override
    public void setLocationFindZ(AngledLocation angledLocation) {

    }

    @Override
    public void setAngle(float v) {

    }

    @Override
    public void setInterior(int i) {

    }

    @Override
    public void setWorld(int i) {

    }

    @Override
    public void setWorldBound(Area area) {

    }

    @Override
    public void sendMessage(Color color, String s) {

    }

    @Override
    public void sendMessage(Color color, String s, Object... objects) {

    }

    @Override
    public void sendChat(Player player, String s) {

    }

    @Override
    public void sendChatToAll(String s) {

    }

    @Override
    public void sendDeathMessage(Player player, Player player1, WeaponModel weaponModel) {

    }

    @Override
    public void sendGameText(int i, int i1, String s) {

    }

    @Override
    public void sendGameText(int i, int i1, String s, Object... objects) {

    }

    @Override
    public void spawn() {

    }

    @Override
    public void setDrunkLevel(int i) {

    }

    @Override
    public int getDrunkLevel() {
        return 0;
    }

    @Override
    public void applyAnimation(String s, String s1, float v, int i, int i1, int i2, int i3, int i4, int i5) {

    }

    @Override
    public void clearAnimations(int i) {

    }

    @Override
    public int getAnimationIndex() {
        return 0;
    }

    @Override
    public void playSound(int i, float v, float v1, float v2) {

    }

    @Override
    public void playSound(int i, Vector3D vector3D) {

    }

    @Override
    public void playSound(int i) {

    }

    @Override
    public void markerForPlayer(Player player, Color color) {

    }

    @Override
    public void showNameTagForPlayer(Player player, boolean b) {

    }

    @Override
    public void kick() {

    }

    @Override
    public void ban() {

    }

    @Override
    public void ban(String s) {

    }

    @Override
    public Menu getCurrentMenu() {
        return null;
    }

    @Override
    public void setCameraPosition(float v, float v1, float v2) {

    }

    @Override
    public void setCameraPosition(Vector3D vector3D) {

    }

    @Override
    public void setCameraLookAt(float v, float v1, float v2, CameraCutStyle cameraCutStyle) {

    }

    @Override
    public void setCameraLookAt(Vector3D vector3D, CameraCutStyle cameraCutStyle) {

    }

    @Override
    public void setCameraLookAt(float v, float v1, float v2) {

    }

    @Override
    public void setCameraLookAt(Vector3D vector3D) {

    }

    @Override
    public void setCameraBehind() {

    }

    @NotNull
    @Override
    public Vector3D getCameraPosition() {
        return null;
    }

    @NotNull
    @Override
    public Vector3D getCameraFrontVector() {
        return null;
    }

    @Override
    public boolean isInAnyVehicle() {
        return false;
    }

    @Override
    public boolean isInVehicle(Vehicle vehicle) {
        return false;
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public boolean isStreamedIn(Player player) {
        return false;
    }

    @Override
    public boolean isNpc() {
        return false;
    }

    @Override
    public void setCheckpoint(Checkpoint checkpoint) {

    }

    @Override
    public void disableCheckpoint() {

    }

    @Override
    public void setRaceCheckpoint(RaceCheckpoint raceCheckpoint) {

    }

    @Override
    public void disableRaceCheckpoint() {

    }

    @Override
    public void setTeam(int i) {

    }

    @Override
    public void setSkin(int i) {

    }

    @NotNull
    @Override
    public WeaponState getWeaponState() {
        return null;
    }

    @Override
    public WeaponData getWeaponData(int i) {
        return null;
    }

    @Override
    public void giveWeapon(WeaponModel weaponModel, int i) {

    }

    @Override
    public void giveWeapon(WeaponData weaponData) {

    }

    @Override
    public void resetWeapons() {

    }

    @NotNull
    @Override
    public Time getTime() {
        return null;
    }

    @Override
    public void setTime(Time time) {

    }

    @Override
    public void toggleClock(boolean b) {

    }

    @Override
    public void forceClassSelection() {

    }

    @Override
    public void setWantedLevel(int i) {

    }

    @Override
    public void playCrimeReport(int i, int i1) {

    }

    @Override
    public void setShopName(ShopName shopName) {

    }

    @Override
    public Vehicle getSurfingVehicle() {
        return null;
    }

    @Override
    public void removeFromVehicle() {

    }

    @Override
    public void toggleControllable(boolean b) {

    }

    @Override
    public void setSpecialAction(SpecialAction specialAction) {

    }

    @NotNull
    @Override
    public PlayerMapIcon getMapIcon() {
        return null;
    }

    @Override
    public void enableStuntBonus(boolean b) {

    }

    @Override
    public void toggleSpectating(boolean b) {

    }

    @Override
    public void spectate(Player player, SpectateMode spectateMode) {

    }

    @Override
    public void spectate(Vehicle vehicle, SpectateMode spectateMode) {

    }

    @Override
    public void startRecord(RecordType recordType, String s) {

    }

    @Override
    public void stopRecord() {

    }

    @Override
    public SampObject getSurfingObject() {
        return null;
    }

    @NotNull
    @Override
    public String getNetworkStats() {
        return null;
    }

    @Override
    public Player getAimedTarget() {
        return null;
    }

    @Override
    public void playAudioStream(String s) {

    }

    @Override
    public void playAudioStream(String s, float v, float v1, float v2, float v3) {

    }

    @Override
    public void playAudioStream(String s, Vector3D vector3D, float v) {

    }

    @Override
    public void playAudioStream(String s, Radius radius) {

    }

    @Override
    public void stopAudioStream() {

    }

    @Override
    public void removeBuilding(int i, float v, float v1, float v2, float v3) {

    }

    @NotNull
    @Override
    public Vector3D getLastShotOrigin() {
        return null;
    }

    @NotNull
    @Override
    public Vector3D getLastShotHitPosition() {
        return null;
    }

    @Override
    public void removeBuilding(int i, Vector3D vector3D, float v) {

    }

    @Override
    public void removeBuilding(int i, Radius radius) {

    }

    @Override
    public void showDialog(DialogId dialogId, DialogStyle dialogStyle, String s, String s1, String s2, String s3) {

    }

    @Override
    public void cancelDialog() {

    }

    @Override
    public boolean editObject(SampObject sampObject) {
        return false;
    }

    @Override
    public boolean editPlayerObject(PlayerObject playerObject) {
        return false;
    }

    @Override
    public void selectObject() {

    }

    @Override
    public void cancelEdit() {

    }

    @Override
    public void attachCameraTo(SampObject sampObject) {

    }

    @Override
    public void attachCameraTo(PlayerObject playerObject) {

    }

    @Override
    public void interpolateCameraPosition(float v, float v1, float v2, float v3, float v4, float v5, int i, CameraCutStyle cameraCutStyle) {

    }

    @Override
    public void interpolateCameraPosition(Vector3D vector3D, Vector3D vector3D1, int i, CameraCutStyle cameraCutStyle) {

    }

    @Override
    public void interpolateCameraLookAt(float v, float v1, float v2, float v3, float v4, float v5, int i, CameraCutStyle cameraCutStyle) {

    }

    @Override
    public void interpolateCameraLookAt(Vector3D vector3D, Vector3D vector3D1, int i, CameraCutStyle cameraCutStyle) {

    }

    @Override
    public void selectTextDraw(Color color) {

    }

    @Override
    public void cancelSelectTextDraw() {

    }

    @Override
    public void createExplosion(float v, float v1, float v2, int i, float v3) {

    }

    @NotNull
    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public LocationZone getMainZoneName() {
        return null;
    }

    @Override
    public LocationZone getZoneName() {
        return null;
    }

    @Override
    public int getConnectedTime() {
        return 0;
    }

    @Override
    public int getMessagesReceived() {
        return 0;
    }

    @Override
    public int getBytesReceived() {
        return 0;
    }

    @Override
    public int getMessagesSent() {
        return 0;
    }

    @Override
    public int getBytesSent() {
        return 0;
    }

    @Override
    public int getMessagesRecvPerSecond() {
        return 0;
    }

    @Override
    public float getPacketLossPercent() {
        return 0;
    }

    @Override
    public int getConnectionStatus() {
        return 0;
    }

    @NotNull
    @Override
    public String getIpPort() {
        return null;
    }

    @Override
    public void setChatBubble(String s, Color color, float v, int i) {

    }

    @Override
    public void setVarInt(String s, int i) {

    }

    @Override
    public int getVarInt(String s) {
        return 0;
    }

    @Override
    public void setVarString(String s, String s1) {

    }

    @NotNull
    @Override
    public String getVarString(String s) {
        return null;
    }

    @Override
    public void setVarFloat(String s, float v) {

    }

    @Override
    public float getVarFloat(String s) {
        return 0;
    }

    @Override
    public boolean deleteVar(String s) {
        return false;
    }

    @NotNull
    @Override
    public List<String> getVarNames() {
        return null;
    }

    @Override
    public PlayerVarType getVarType(String s) {
        return null;
    }

    @Override
    public void disableRemoteVehicleCollisions(boolean b) {

    }

    @Override
    public void enablePlayerCameraTarget(boolean b) {

    }

    @Override
    public Actor getCameraTargetActor() {
        return null;
    }

    @Override
    public SampObject getCameraTargetObject() {
        return null;
    }

    @Override
    public Player getCameraTargetPlayer() {
        return null;
    }

    @Override
    public Vehicle getCameraTargetVehicle() {
        return null;
    }

    @Override
    public Actor getTargetActor() {
        return null;
    }

    @Override
    public void setWeather(Weather weather) {

    }

    @Override
    public int getInterior() {
        return 0;
    }

    @Override
    public int getWorld() {
        return 0;
    }

    @Override
    public float getFacingAngle() {
        return 0;
    }

    @Override
    public void setFacingAngle(float v) {

    }

    @Override
    public void setVelocity(Velocity velocity) {

    }

    @Override
    public void setStuntBonusEnabled(boolean b) {

    }

    @Override
    public void setSpectating(boolean b) {

    }

    @Override
    public void setRecording(boolean b) {

    }

    @Override
    public void setControllable(boolean b) {

    }

    @Override
    public void setSpawnInfo(float v, float v1, float v2, int i, int i1, float v3, int i2, int i3, WeaponData weaponData, WeaponData weaponData1, WeaponData weaponData2) {

    }

    @NotNull
    @Override
    public Weather getWeather() {
        return null;
    }

    @Override
    public boolean isDestroyed() {
        return false;
    }

    @Override
    public void sendMessage(String s) {

    }
}
