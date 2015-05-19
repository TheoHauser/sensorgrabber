filename = 'C:\Users\ABC\Desktop\Dropbox\UG_research\Waqas\data_with_plots\4-14-2015\1429046329147.csv';
M = csvread(filename);
time = M(:,1);
azimuth = M(:,2);
pitch = M(:,3);
roll = M(:,4);

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
