filename = '/home/hauser2016/Project/sensorgrabber/matlabCode/CSV Data/06-23-2015 10:34:40';
M = csvread(filename);
time = M(:,1);
azimuth = M(:,3);
pitch = M(:,4);
roll = M(:,5);

%converting from rad to deg
azi = rad2deg(azimuth);
%pit = rad2deg(pitch);
%rol = rad2deg(roll);

%smoothing
azis = smooth(azi,0.3,'rloess');
%pits = smooth(pit,0.3,'rloess');
%rols = smooth(rol,0.3,'rloess');

%unfiltered plot
figure(1);
plot(time,azi,'-O');
axis([0 inf -10 370]); 

hold on

%smooth plot
figure(2);
plot(time,azis,'-O');
axis([0 inf -10 370]);
