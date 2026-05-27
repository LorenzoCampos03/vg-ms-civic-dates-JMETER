-- Agregando campos para notificaciones programadas y masivas
ALTER TABLE event ADD COLUMN notification_channels TEXT[];
ALTER TABLE event ADD COLUMN custom_message TEXT;
ALTER TABLE event ADD COLUMN target_roles TEXT[];
ALTER TABLE event ADD COLUMN is_notification_scheduled BOOLEAN DEFAULT false;
