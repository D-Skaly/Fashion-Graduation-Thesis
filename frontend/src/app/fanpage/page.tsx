'use client'

import { useMemo, useState } from 'react'
import { Camera, Mail, MapPin, Phone, Save, User } from 'lucide-react'

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'

export default function FanpageProfileManager() {
  const [profile, setProfile] = useState({
    fullName: 'Nguyễn Minh Anh',
    nickname: 'Mina',
    bio: 'Lifestyle creator | Yêu thời trang và du lịch. Chia sẻ những khoảnh khắc đời thường tích cực mỗi ngày.',
    email: 'mina.fashion@example.com',
    phone: '+84 912 345 678',
    location: 'TP. Hồ Chí Minh, Việt Nam',
    avatarUrl: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&h=400&fit=crop',
    hobbies: 'Fashion, Photography, Travel',
  })
  const [savedAt, setSavedAt] = useState<Date | null>(null)

  const initials = useMemo(
    () =>
      profile.fullName
        .split(' ')
        .filter(Boolean)
        .map((word) => word[0])
        .slice(-2)
        .join('')
        .toUpperCase(),
    [profile.fullName],
  )

  const handleChange = (key: keyof typeof profile, value: string) => {
    setProfile((prev) => ({ ...prev, [key]: value }))
  }

  const handleSave = () => {
    setSavedAt(new Date())
  }

  return (
    <main className="container mx-auto max-w-5xl px-4 py-8">
      <div className="mb-6 flex flex-col gap-2">
        <h1 className="text-3xl font-bold tracking-tight">Fanpage cá nhân</h1>
        <p className="text-muted-foreground">
          Trang quản lý thông tin cá nhân cho người dùng fanpage. Bạn có thể chỉnh sửa hồ sơ và xem trước hiển thị.
        </p>
      </div>

      <div className="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
        <Card>
          <CardHeader>
            <CardTitle>Chỉnh sửa thông tin</CardTitle>
          </CardHeader>
          <CardContent className="space-y-5">
            <div className="grid gap-4 sm:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="fullName">Họ và tên</Label>
                <Input id="fullName" value={profile.fullName} onChange={(e) => handleChange('fullName', e.target.value)} />
              </div>
              <div className="space-y-2">
                <Label htmlFor="nickname">Biệt danh</Label>
                <Input id="nickname" value={profile.nickname} onChange={(e) => handleChange('nickname', e.target.value)} />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="bio">Giới thiệu</Label>
              <Textarea id="bio" rows={4} value={profile.bio} onChange={(e) => handleChange('bio', e.target.value)} />
            </div>

            <div className="grid gap-4 sm:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input id="email" type="email" value={profile.email} onChange={(e) => handleChange('email', e.target.value)} />
              </div>
              <div className="space-y-2">
                <Label htmlFor="phone">Số điện thoại</Label>
                <Input id="phone" value={profile.phone} onChange={(e) => handleChange('phone', e.target.value)} />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="location">Địa chỉ</Label>
              <Input id="location" value={profile.location} onChange={(e) => handleChange('location', e.target.value)} />
            </div>

            <div className="space-y-2">
              <Label htmlFor="avatarUrl">Link ảnh đại diện</Label>
              <Input id="avatarUrl" value={profile.avatarUrl} onChange={(e) => handleChange('avatarUrl', e.target.value)} />
            </div>

            <div className="space-y-2">
              <Label htmlFor="hobbies">Sở thích</Label>
              <Input id="hobbies" value={profile.hobbies} onChange={(e) => handleChange('hobbies', e.target.value)} />
            </div>

            <div className="flex items-center justify-between gap-4">
              <Button onClick={handleSave} className="gap-2">
                <Save className="h-4 w-4" /> Lưu thay đổi
              </Button>
              {savedAt && (
                <span className="text-sm text-muted-foreground">Đã lưu lúc {savedAt.toLocaleTimeString('vi-VN')}</span>
              )}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Xem trước fanpage</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center gap-3">
              <Avatar className="h-16 w-16">
                <AvatarImage src={profile.avatarUrl} alt={profile.fullName} />
                <AvatarFallback>{initials || 'NA'}</AvatarFallback>
              </Avatar>
              <div>
                <h2 className="text-lg font-semibold">{profile.fullName}</h2>
                <p className="text-sm text-muted-foreground">@{profile.nickname.replaceAll(' ', '').toLowerCase()}</p>
              </div>
            </div>

            <Badge variant="secondary" className="gap-2">
              <User className="h-3 w-3" /> Creator
            </Badge>

            <p className="text-sm leading-relaxed">{profile.bio}</p>

            <div className="space-y-2 text-sm text-muted-foreground">
              <p className="flex items-center gap-2"><Mail className="h-4 w-4" /> {profile.email}</p>
              <p className="flex items-center gap-2"><Phone className="h-4 w-4" /> {profile.phone}</p>
              <p className="flex items-center gap-2"><MapPin className="h-4 w-4" /> {profile.location}</p>
              <p className="flex items-center gap-2"><Camera className="h-4 w-4" /> {profile.hobbies}</p>
            </div>
          </CardContent>
        </Card>
      </div>
    </main>
  )
}
