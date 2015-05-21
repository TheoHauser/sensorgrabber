filename = '/home/hauser2016/Project/sensorgrabber/pythonCode/data_with_plots/4-15-2015/05-20-2015 14:49:40.csv';
M = csvread(filename,0,1);
time = M(:,7);
azimuth = M(:,1);
pitch = M(:,2);
roll = M(:,3);

%converting from rad to deg
azi = rad2deg(azimuth);
pit = rad2deg(pitch);
rol = rad2deg(roll);

%smoothing
azis = smooth(azi,0.3,'rloess');
pits = smooth(pit,0.3,'rloess');
rols = smooth(rol,0.3,'rloess');

%unfiltered plot
figure(1);
plot(time,azi,'-O', time, pit, '-*', time, rol, '-X');

hold on

%smooth plot
figure(2);
plot(time,azis,'-O', time, pits, '-*', time, rols, '-X');
